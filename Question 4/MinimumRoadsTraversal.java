import java.util.*;

public class MinimumRoadsTraversal {
    public static int minRoadsToTraverse(int[] packages, int[][] roads) {
        int n = packages.length;
        List<List<Integer>> graph = new ArrayList<>();
        
        // Initialize adjacency list
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        
        // Build the graph
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]);
            graph.get(road[1]).add(road[0]);
        }

        // Find all nodes with packages
        Set<Integer> packageNodes = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                packageNodes.add(i);
            }
        }
        
        if (packageNodes.isEmpty()) return 0; // No packages to collect
        
        // Find the minimal subgraph that connects all package nodes using BFS
        int totalEdges = 0;
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        // Start BFS from any package node
        int start = packageNodes.iterator().next();
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            
            for (int neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    
                    // If it's a relevant node (either a package or a path between them), count the edge
                    if (packageNodes.contains(neighbor) || packageNodes.contains(node)) {
                        totalEdges++;
                    }
                }
            }
        }
        
        // Since we must return to the start, we multiply by 2
        return totalEdges * 2;
    }

    public static void main(String[] args) {
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println(minRoadsToTraverse(packages1, roads1)); // Output: 2

        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println(minRoadsToTraverse(packages2, roads2)); // Output: 2
    }
}

