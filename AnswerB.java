import java.util.*;

public class AnswerB {

    public static List<Integer> findNodesWithOnlyTargetAsParent(int[][] edges, int target) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, Integer> inDegree = new HashMap<>();

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }

        List<Integer> result = new ArrayList<>();
        dfs(graph, inDegree, target, target, result);

        return result;
    }

    private static void dfs(Map<Integer, List<Integer>> graph, Map<Integer, Integer> inDegree, int node, int target,
            List<Integer> result) {

        if (inDegree.getOrDefault(node, 0) == 1 && node != target) {
            result.add(node);
        }

        if (graph.containsKey(node)) {
            for (int child : graph.get(node)) {
                dfs(graph, inDegree, child, target, result);
            }
        }
    }

    public static void main(String[] args) {
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 1, 6 }, { 2, 4 }, { 4, 6 }, { 4, 5 }, { 5, 7 } };
        int target = 4;

        List<Integer> uniqueParents = findNodesWithOnlyTargetAsParent(edges, target);

        System.out.print("Nodes whose only parent is " + target + ": {");
        for (int i = 0; i < uniqueParents.size(); i++) {
            System.out.print(uniqueParents.get(i));
            if (i < uniqueParents.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }
}