package kz.aitu.daa.assignment4;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringMatchingTest {
    private void check(String text, String pattern, List<Integer> expected) {
        assertEquals(expected, StringMatching.naive(text, pattern).positions());
        assertEquals(expected, StringMatching.kmp(text, pattern).positions());
        assertEquals(expected, StringMatching.rabinKarp(text, pattern).positions());
    }

    @Test
    void beginning() {
        check("abcdef", "abc", List.of(0));
    }

    @Test
    void middle() {
        check("xxabcxx", "abc", List.of(2));
    }

    @Test
    void end() {
        check("abcdef", "def", List.of(3));
    }

    @Test
    void multipleAndOverlappingOccurrences() {
        check("aaaaa", "aaa", List.of(0, 1, 2));
        check("abababa", "aba", List.of(0, 2, 4));
    }

    @Test
    void absent() {
        check("abcdef", "xyz", List.of());
    }

    @Test
    void patternLongerThanText() {
        check("ab", "abc", List.of());
    }

    @Test
    void emptyPattern() {
        check("abc", "", List.of(0, 1, 2, 3));
        check("", "", List.of(0));
    }

    @Test
    void emptyTextAndEqualStrings() {
        check("", "a", List.of());
        check("abc", "abc", List.of(0));
    }

    @Test
    void prefixFallback() {
        check("abababacaba", "ababaca", List.of(2));
        assertArrayEquals(new int[]{0, 0, 1, 2, 3, 0, 1},
                StringMatching.buildLps("ababaca"));
    }

    @Test
    void hashCollisionDoesNotCreateFalseMatch() {
        // Both two-character hashes equal 25088 with base 256.
        check("b\u0000", "a\u0100", List.of());
        assertEquals(1, StringMatching.rabinKarp("b\u0000", "a\u0100").comparisons());
    }

    @Test
    void comparisonCounters() {
        assertEquals(3, StringMatching.naive("abc", "b").comparisons());
        assertEquals(3, StringMatching.kmp("abc", "b").comparisons());
        assertEquals(1, StringMatching.rabinKarp("abc", "b").comparisons());
        assertEquals(0, StringMatching.rabinKarp("abc", "x").comparisons());
        check("abc", "b", List.of(1));
        check("abc", "x", List.of());
    }

    @Test
    void kmpAvoidsRepeatedComparisons() {
        String text = "a".repeat(1000);
        String pattern = "a".repeat(49) + "b";
        check(text, pattern, List.of());
        assertEquals(47_550, StringMatching.naive(text, pattern).comparisons());
        assertTrue(StringMatching.kmp(text, pattern).comparisons() <= 2 * text.length());
    }

    @Test
    void randomCasesAgainstDirectCheck() {
        Random random = new Random(42);
        for (int test = 0; test < 300; test++) {
            String text = randomString(random, random.nextInt(80));
            String pattern = randomString(random, random.nextInt(12));
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i <= text.length() - pattern.length(); i++) {
                if (text.startsWith(pattern, i)) {
                    expected.add(i);
                }
            }
            check(text, pattern, expected);
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append("abc".charAt(random.nextInt(3)));
        }
        return text.toString();
    }
}
