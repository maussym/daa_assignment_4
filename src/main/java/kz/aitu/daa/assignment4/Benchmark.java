package kz.aitu.daa.assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    private static final String ALPHABET = "abcd";
    private static final int RUNS = 5;
    private static volatile Object lastResult;

    public static void run() throws IOException {
        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            csv.println("algorithm,case,n,m,runs,average_ms,operations,operation_type,result");
            for (int n : new int[]{100, 500, 1000}) {
                Random random = new Random(42 + n);
                String a = randomText(random, n);
                String b = randomText(random, n);
                double lcsTime = averageTime(() -> DynamicProgramming.lcs(a, b));
                row(csv, "LCS", "random", n, n, lcsTime,
                        (long) n * n, "dp_cells", DynamicProgramming.lcs(a, b));
                double editTime = averageTime(() -> DynamicProgramming.editDistance(a, b));
                row(csv, "EditDistance", "random", n, n, editTime,
                        (long) n * n, "dp_cells", DynamicProgramming.editDistance(a, b));
            }

            for (int n : new int[]{10_000, 50_000, 100_000}) {
                Random random = new Random(42 + n);
                String text = randomText(random, n);
                String present = text.substring(n / 2, n / 2 + 32);
                String absent;
                do {
                    absent = randomText(random, 32);
                } while (!StringMatching.naive(text, absent).positions().isEmpty());
                matching(csv, text, present, "present");
                matching(csv, text, absent, "absent");
            }

            for (int n : new int[]{1000, 5000, 10_000}) {
                String text = randomText(new Random(42 + n), n);
                int[] suffixArray = suffixArray(text);
                Kasai.Result result = Kasai.build(text, suffixArray);
                double time = averageTime(() -> Kasai.build(text, suffixArray));
                row(csv, "Kasai", "random", n, 0, time,
                        result.comparisons(), "char_comparisons", result.lcp().length);
            }
            if (csv.checkError()) {
                throw new IOException("Could not write results.csv");
            }
        }
        System.out.println("Saved " + output + " (27 experiments, 5 timed runs each)");
    }

    private static void matching(PrintWriter csv, String text, String pattern, String inputCase) {
        StringMatching.Result naive = StringMatching.naive(text, pattern);
        StringMatching.Result kmp = StringMatching.kmp(text, pattern);
        StringMatching.Result rk = StringMatching.rabinKarp(text, pattern);
        if (!naive.positions().equals(kmp.positions()) || !naive.positions().equals(rk.positions())) {
            throw new IllegalStateException("String matching results disagree");
        }
        double naiveTime = averageTime(() -> StringMatching.naive(text, pattern));
        double kmpTime = averageTime(() -> StringMatching.kmp(text, pattern));
        double rkTime = averageTime(() -> StringMatching.rabinKarp(text, pattern));
        int n = text.length();
        int m = pattern.length();
        row(csv, "Naive", inputCase, n, m, naiveTime,
                naive.comparisons(), "char_comparisons", naive.positions().size());
        row(csv, "KMP", inputCase, n, m, kmpTime,
                kmp.comparisons(), "char_comparisons", kmp.positions().size());
        row(csv, "RabinKarp", inputCase, n, m, rkTime,
                rk.comparisons(), "char_comparisons", rk.positions().size());
    }

    private static double averageTime(Supplier<?> algorithm) {
        for (int i = 0; i < 5; i++) {
            lastResult = algorithm.get();
        }
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            Object result = algorithm.get();
            total += System.nanoTime() - start;
            lastResult = result;
        }
        return total / (RUNS * 1_000_000.0);
    }

    private static void row(PrintWriter csv, String algorithm, String inputCase,
                            int n, int m, double time, long operations, String type, int result) {
        csv.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%.6f,%d,%s,%d%n",
                algorithm, inputCase, n, m, RUNS, time, operations, type, result);
    }

    private static String randomText(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return text.toString();
    }

    // Simple suffix sorting for experiment setup, never included in Kasai timing.
    static int[] suffixArray(String text) {
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> {
            int i = a;
            int j = b;
            while (i < n && j < n) {
                int difference = Character.compare(text.charAt(i), text.charAt(j));
                if (difference != 0) {
                    return difference;
                }
                i++;
                j++;
            }
            return Integer.compare(n - a, n - b);
        });
        return Arrays.stream(order).mapToInt(Integer::intValue).toArray();
    }
}
