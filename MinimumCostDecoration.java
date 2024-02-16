public class MinimumCostDecoration {

    public static int minCostToDecorate(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) {
            return 0;
        }

        int n = costs.length;
        int k = costs[0].length;

        int[][] dp = new int[n][k];

        for (int j = 0; j < k; j++) {
            dp[0][j] = costs[0][j];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < k; j++) {
                dp[i][j] = Integer.MAX_VALUE;
                for (int prevTheme = 0; prevTheme < k; prevTheme++) {
                    if (prevTheme != j) {
                        dp[i][j] = Math.min(dp[i][j], dp[i - 1][prevTheme] + costs[i][j]);
                    }
                }
            }
        }

        int minCost = Integer.MAX_VALUE;
        for (int j = 0; j < k; j++) {
            minCost = Math.min(minCost, dp[n - 1][j]);
        }
        return minCost;
    }

    public static void main(String[] args) {
        int[][] costMatrix = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        int result = minCostToDecorate(costMatrix);
        System.out.println("MinCostToDecorate: " + result); 
    }
}