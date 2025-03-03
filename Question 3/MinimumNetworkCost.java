import java.util.*;

class UnionFind {
    private int[] parent, rank;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) return false;
        
        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}

public class MinimumNetworkCost {
    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();
        
        // Step 1: Add virtual edges from node 0 to all devices with module cost
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{0, i + 1, modules[i]});
        }
        
        // Step 2: Add given connections
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0], conn[1], conn[2]});
        }
        
        // Step 3: Sort edges by cost
        edges.sort(Comparator.comparingInt(a -> a[2]));
        
        // Step 4: Apply Kruskal's MST algorithm
        UnionFind uf = new UnionFind(n + 1); // Including virtual node 0
        int totalCost = 0, numEdges = 0;
        
        for (int[] edge : edges) {
            if (uf.union(edge[0], edge[1])) {
                totalCost += edge[2];
                numEdges++;
                if (numEdges == n) return totalCost;
            }
        }
        
        return -1; // If not all devices are connected
    }
    
    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minTotalCost(n, modules, connections)); // Output: 3
    }
}