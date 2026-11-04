package kz.aitu.daa.assignment4;

public class Kasai {
    public record Result(int[] lcp, long comparisons) {}

    public static Result build(String text, int[] suffixArray) {
        int n = text.length();
        if (suffixArray.length != n) {
            throw new IllegalArgumentException("Suffix array length must match text length");
        }
        int[] rank = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int suffix = suffixArray[i];
            if (suffix < 0 || suffix >= n || seen[suffix]) {
                throw new IllegalArgumentException("Suffix array must be a permutation of 0..n-1");
            }
            seen[suffix] = true;
            rank[suffix] = i;
        }

        int[] lcp = new int[n];
        int length = 0;
        long comparisons = 0;
        for (int i = 0; i < n; i++) {
            int position = rank[i];
            if (position == 0) {
                length = 0;
                continue;
            }
            int j = suffixArray[position - 1];
            while (i + length < n && j + length < n) {
                comparisons++;
                if (text.charAt(i + length) != text.charAt(j + length)) {
                    break;
                }
                length++;
            }
            lcp[position] = length;
            if (length > 0) {
                length--;
            }
        }
        return new Result(lcp, comparisons);
    }
}
