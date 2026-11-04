package kz.aitu.daa.assignment4;

import java.util.ArrayList;
import java.util.List;

public class StringMatching {
    private static final long BASE = 256;
    private static final long MOD = 1_000_000_007;

    public record Result(List<Integer> positions, long comparisons) {}

    public static Result naive(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> positions = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == m) {
                positions.add(i);
            }
        }
        return new Result(positions, comparisons);
    }

    public static Result kmp(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> positions = new ArrayList<>();
        if (m > n) {
            return new Result(positions, 0);
        }
        int[] lps = buildLps(pattern);
        int i = 0;
        int j = 0;
        long comparisons = 0;
        while (i < n) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    positions.add(i - m);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                // Keep the text position and reuse the matched prefix.
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return new Result(positions, comparisons);
    }

    static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    public static Result rabinKarp(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> positions = new ArrayList<>();
        if (m > n) {
            return new Result(positions, 0);
        }
        long power = 1;
        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
            if (i < m - 1) {
                power = power * BASE % MOD;
            }
        }

        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            if (windowHash == patternHash) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        break;
                    }
                    j++;
                }
                if (j == m) {
                    positions.add(i);
                }
            }
            if (i < n - m) {
                // Remove the first character, shift, then add the next one.
                windowHash = (windowHash - text.charAt(i) * power % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return new Result(positions, comparisons);
    }

    private static Result emptyPattern(int n) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            positions.add(i);
        }
        return new Result(positions, 0);
    }
}
