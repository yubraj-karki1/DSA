public class kthsmallestproduct {

    public static int findKthSmallestProduct(int[] returns1, int[] returns2, int k) {
        // Define the search space using int (risk of overflow if numbers are large)
        int left = returns1[0] * returns2[0];
        int right = returns1[returns1.length - 1] * returns2[returns2.length - 1];

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (countPairs(returns1, returns2, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private static int countPairs(int[] returns1, int[] returns2, int target) {
        int count = 0;
        for (int num1 : returns1) {
            int low = 0, high = returns2.length;
            while (low < high) {
                int mid = (low + high) / 2;
                if (num1 * returns2[mid] <= target) { 
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            count += low;
        }
        return count;
    }

    public static void main(String[] args) {
        int[] returns1 = {2, 5};
        int[] returns2 = {3, 4};
        int k = 2;
        System.out.println(findKthSmallestProduct(returns1, returns2, k));  // Output: 8

        int[] returns1_2 = {-4, -2, 0, 3};
        int[] returns2_2 = {2, 4};
        int k2 = 6;
        System.out.println(findKthSmallestProduct(returns1_2, returns2_2, k2));  // Output: 0
    }
}