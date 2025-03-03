import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;

public class NetworkGUI extends JFrame {
    private GraphPanel graphPanel;
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private JLabel costLabel, latencyLabel;

    public NetworkGUI() {
        setTitle("Network Topology Optimizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Graph visualization panel
        graphPanel = new GraphPanel();
        mainPanel.add(graphPanel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(6, 1));
        
        JButton addNodeButton = new JButton("Add Node");
        JButton addEdgeButton = new JButton("Add Edge");
        JButton optimizeButton = new JButton("Optimize Network");
        JButton shortestPathButton = new JButton("Find Shortest Path");
        
        costLabel = new JLabel("Total Cost: 0");
        latencyLabel = new JLabel("Average Latency: 0");
        
        controlPanel.add(addNodeButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(optimizeButton);
        controlPanel.add(shortestPathButton);
        controlPanel.add(costLabel);
        controlPanel.add(latencyLabel);
        
        mainPanel.add(controlPanel, BorderLayout.EAST);
        add(mainPanel);
        
        // Event listeners
        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        optimizeButton.addActionListener(e -> optimizeNetwork());
        shortestPathButton.addActionListener(e -> findShortestPath());
        
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                graphPanel.handleClick(e);
            }
        });
    }

    // Node class (made comparable for PriorityQueue)
    class Node implements Comparable<Node> {
        int x, y;
        String id;
        boolean isServer;
        
        Node(int x, int y, String id, boolean isServer) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.isServer = isServer;
        }

        @Override
        public int compareTo(Node other) {
            return this.id.compareTo(other.id); // Compare by ID for uniqueness in PriorityQueue
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return id.equals(node.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    // Edge class
    class Edge {
        Node from, to;
        int cost, bandwidth;
        
        Edge(Node from, Node to, int cost, int bandwidth) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    // Graph visualization panel
    class GraphPanel extends JPanel {
        private List<Edge> shortestPathEdges = new ArrayList<>(); // To store edges in shortest path for visualization
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw edges (highlight shortest path if available)
            for (Edge edge : edges) {
                g.setColor(shortestPathEdges.contains(edge) ? Color.RED : Color.BLACK);
                g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
                int midX = (edge.from.x + edge.to.x) / 2;
                int midY = (edge.from.y + edge.to.y) / 2;
                g.drawString("C:" + edge.cost + " B:" + edge.bandwidth, midX, midY);
            }
            // Draw nodes
            for (Node node : nodes) {
                g.setColor(node.isServer ? Color.BLUE : Color.GREEN);
                g.fillOval(node.x - 10, node.y - 10, 20, 20);
                g.setColor(Color.BLACK);
                g.drawString(node.id, node.x - 5, node.y - 15);
            }
        }
        
        void handleClick(MouseEvent e) {
            repaint();
        }
        
        public void setShortestPath(List<Edge> pathEdges) {
            shortestPathEdges = pathEdges;
            repaint();
        }
    }

    private void addNode() {
        String id = JOptionPane.showInputDialog("Enter node ID:");
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Node ID cannot be empty!");
            return;
        }
        boolean isServer = JOptionPane.showConfirmDialog(null, 
            "Is this a server?", "Node Type", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        
        Point position = calculateNodePosition(nodes.size());
        nodes.add(new Node(position.x, position.y, id, isServer));
        graphPanel.repaint();
    }

    private Point calculateNodePosition(int nodeIndex) {
        int centerX = graphPanel.getWidth() / 2;
        int centerY = graphPanel.getHeight() / 2;
        int radius = Math.min(graphPanel.getWidth(), graphPanel.getHeight()) / 4; // Reduced from /3 to /4
        
        if (nodeIndex == 0) {
            return new Point(centerX, centerY); // First node at center
        }
        
        int numNodes = nodes.size() + 1;
        if (numNodes <= 3) {
            // Triangle arrangement
            double angle = 2 * Math.PI * nodeIndex / 3 - Math.PI / 2; // Offset by -90 degrees
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            return new Point(x, y);
        } else if (numNodes <= 4) {
            // Square arrangement with adjusted positions
            switch(nodeIndex) {
                case 1: return new Point(centerX - radius, centerY - radius); // Top-left
                case 2: return new Point(centerX + radius, centerY - radius); // Top-right
                case 3: return new Point(centerX - radius, centerY + radius); // Bottom-left
                default: return new Point(centerX + radius, centerY + radius); // Bottom-right
            }
        } else {
            // Hexagonal or circular arrangement for more nodes
            double angle = (2 * Math.PI * nodeIndex / Math.max(6, numNodes)) - Math.PI / 2; // Offset by -90 degrees
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            return new Point(x, y);
        }
    }

    private void addEdge() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 nodes!");
            return;
        }
        
        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String from = (String)JOptionPane.showInputDialog(this, "From:", "Add Edge", 
            JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String to = (String)JOptionPane.showInputDialog(this, "To:", "Add Edge", 
            JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[1]);
        
        if (from == null || to == null || from.trim().isEmpty() || to.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Node IDs cannot be empty!");
            return;
        }
        
        try {
            int cost = Integer.parseInt(JOptionPane.showInputDialog("Enter cost:"));
            int bandwidth = Integer.parseInt(JOptionPane.showInputDialog("Enter bandwidth:"));
            if (cost < 0 || bandwidth <= 0) {
                JOptionPane.showMessageDialog(this, "Cost must be non-negative, and bandwidth must be positive!");
                return;
            }
            
            Node fromNode = nodes.stream().filter(n -> n.id.equals(from)).findFirst().get();
            Node toNode = nodes.stream().filter(n -> n.id.equals(to)).findFirst().get();
            
            edges.add(new Edge(fromNode, toNode, cost, bandwidth));
            updateMetrics();
            graphPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for cost and bandwidth!");
        }
    }

    private void optimizeNetwork() {
        // Simple MST-based optimization (Kruskal's algorithm could be implemented here)
        ArrayList<Edge> mst = new ArrayList<>();
        Collections.sort(edges, Comparator.comparingInt(e -> e.cost));
        
        // Very basic optimization - takes lowest cost edges
        Set<String> connected = new HashSet<>();
        for (Edge e : edges) {
            if (mst.size() == nodes.size() - 1) break;
            if (!connected.contains(e.from.id) || !connected.contains(e.to.id)) {
                mst.add(e);
                connected.add(e.from.id);
                connected.add(e.to.id);
            }
        }
        
        edges = mst;
        updateMetrics();
        graphPanel.repaint();
    }

    private void findShortestPath() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 nodes!");
            return;
        }
        
        if (edges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No connections exist in the network! Add edges first.");
            return;
        }
        
        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String from = (String)JOptionPane.showInputDialog(this, "From:", "Shortest Path", 
            JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String to = (String)JOptionPane.showInputDialog(this, "To:", "Shortest Path", 
            JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[1]);
        
        if (from == null || to == null || from.trim().isEmpty() || to.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Node IDs cannot be empty!");
            return;
        }
        
        Node start = nodes.stream().filter(n -> n.id.equals(from)).findFirst()
                         .orElseThrow(() -> new IllegalArgumentException("Source node not found: " + from));
        Node end = nodes.stream().filter(n -> n.id.equals(to)).findFirst()
                       .orElseThrow(() -> new IllegalArgumentException("Destination node not found: " + to));
        
        // Debug: Print nodes and edges for troubleshooting
        System.out.println("Finding path from " + start.id + " to " + end.id);
        System.out.println("Edges: " + edges.size());
        
        // Dijkstra's algorithm using latency (1000/bandwidth) as weight
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>((n1, n2) -> {
            double d1 = distances.getOrDefault(n1, Double.MAX_VALUE);
            double d2 = distances.getOrDefault(n2, Double.MAX_VALUE);
            return Double.compare(d1, d2);
        });
        
        for (Node node : nodes) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(start);
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            System.out.println("Processing node: " + current.id + " with distance: " + distances.get(current));
            if (current == end) break;
            
            for (Edge edge : edges) {
                Node neighbor = (edge.from == current) ? edge.to : (edge.to == current) ? edge.from : null;
                if (neighbor != null) {
                    double latency = 1000.0 / edge.bandwidth;
                    double newDist = distances.get(current) + latency;
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        previous.put(neighbor, current);
                        pq.add(neighbor);
                    }
                }
            }
        }
        
        // Reconstruct path
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        
        if (path.size() < 2) {
            JOptionPane.showMessageDialog(this, "No path exists between " + from + " and " + to);
            return;
        }
        
        // Convert path to edges for visualization
        List<Edge> pathEdges = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Node fromNode = path.get(i);
            Node toNode = path.get(i + 1);
            for (Edge edge : edges) {
                if ((edge.from == fromNode && edge.to == toNode) || (edge.from == toNode && edge.to == fromNode)) {
                    pathEdges.add(edge);
                    break;
                }
            }
        }
        
        // Display path and latency
        String pathStr = path.stream().map(n -> n.id).collect(Collectors.joining(" → "));
        double totalLatency = distances.get(end);
        JOptionPane.showMessageDialog(this, 
            "Shortest path: " + pathStr + "\nLatency: " + String.format("%.2f ms", totalLatency));
        
        // Highlight path visually
        graphPanel.setShortestPath(pathEdges);
        graphPanel.repaint();
    }

    private void updateMetrics() {
        int totalCost = edges.stream().mapToInt(e -> e.cost).sum();
        double avgLatency = edges.stream().mapToDouble(e -> 1000.0 / e.bandwidth).average().orElse(0);
        
        costLabel.setText("Total Cost: " + totalCost);
        latencyLabel.setText(String.format("Average Latency: %.2f ms", avgLatency));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NetworkGUI().setVisible(true);
        });
    }
}


