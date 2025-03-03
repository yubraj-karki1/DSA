public class CriticalTemperature {
    public static int minTests(int k, int n) {
        // Edge case: If there are no temperature levels, no tests are needed
        if (n == 0) {
            return 0;
        }
        
        // Edge case: If there is only one sample, we must check one by one
        if (k == 1) {
            return n;
        }
        
        // DP table where dp[k][m] represents the maximum n that can be checked
        int[][] dp = new int[k + 1][n + 1];
        
        int m = 0;  // Number of attempts
        while (dp[k][m] < n) {
            m++;
            for (int i = 1; i <= k; i++) {
                dp[i][m] = dp[i - 1][m - 1] + dp[i][m - 1] + 1;
            }
        }
        
        return m;
    }

    public static void main(String[] args) {
        System.out.println(minTests(1, 2));  // Output: 2
        System.out.println(minTests(2, 6));  // Output: 3
        System.out.println(minTests(3, 14)); // Output: 4
    }
}

