import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}

class Graph {
    ArrayList<ArrayList<Edge>> adjList;

    public Graph(int V) {
        adjList = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int src, int dest, int weight) {
        adjList.get(src).add(new Edge(src, dest, weight));
        adjList.get(dest).add(new Edge(dest, src, weight)); 
    }

    public ArrayList<Edge> kruskalMST() {
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        int[] parent = new int[adjList.size()];
        int[] rank = new int[adjList.size()];

        for (int i = 0; i < adjList.size(); i++) {
            parent[i] = i;
        }

        for (int i = 0; i < adjList.size(); i++) {
            for (Edge edge : adjList.get(i)) {
                pq.add(edge);
            }
        }

        ArrayList<Edge> mst = new ArrayList<>();

        while (!pq.isEmpty() && mst.size() < adjList.size() - 1) {
            Edge edge = pq.poll();
            int x = find(parent, edge.src);
            int y = find(parent, edge.dest);

            if (x != y) {
                mst.add(edge);
                union(parent, rank, x, y);
            }
        }

        return mst;
    }

    int find(int[] parent, int i) {
        if (parent[i] == i) {
            return i;
        }
        return find(parent, parent[i]);
    }

    void union(int[] parent, int[] rank, int x, int y) {
        int xroot = find(parent, x);
        int yroot = find(parent, y);

        if (rank[xroot] < rank[yroot]) {
            parent[xroot] = yroot;
        } else if (rank[xroot] > rank[yroot]) {
            parent[yroot] = xroot;
        } else {
            parent[yroot] = xroot;
            rank[xroot]++;
        }
    }
}

public class KruskalMST {
    public static void main(String[] args) {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 2, 4);
        graph.addEdge(2, 3, 8);

        ArrayList<Edge> mst = graph.kruskalMST();

        System.out.println("Minimum Spanning Tree Edges:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " - " + edge.dest + " (" + edge.weight + ")");
        }
    }
}
