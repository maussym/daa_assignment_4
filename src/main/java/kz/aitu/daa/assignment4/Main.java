package kz.aitu.daa.assignment4;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("bench")) {
            Benchmark.run();
            return;
        }
        System.out.println("Assignment 4: Text Analysis");
        System.out.println("Maussymzhan Makazhan, SE-2527");
        System.out.println("LCS(abcde, ace) = " + DynamicProgramming.lcs("abcde", "ace"));
        System.out.println("Edit distance(kitten, sitting) = "
                + DynamicProgramming.editDistance("kitten", "sitting"));

        String text = "abababa";
        String pattern = "aba";
        System.out.println("Text: " + text + ", pattern: " + pattern);
        System.out.println("Naive: " + StringMatching.naive(text, pattern).positions());
        System.out.println("KMP: " + StringMatching.kmp(text, pattern).positions());
        System.out.println("Rabin-Karp: " + StringMatching.rabinKarp(text, pattern).positions());

        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        System.out.println("banana suffix array: " + Arrays.toString(suffixArray));
        System.out.println("banana LCP: " + Arrays.toString(Kasai.build("banana", suffixArray).lcp()));
    }
}
