package kz.aitu.daa.assignment4;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KasaiTest {
    @Test
    void suffixArrayForExperimentSetup() {
        assertArrayEquals(new int[]{5, 3, 1, 0, 4, 2}, Benchmark.suffixArray("banana"));
        assertArrayEquals(new int[]{3, 2, 1, 0}, Benchmark.suffixArray("aaaa"));
        assertArrayEquals(new int[0], Benchmark.suffixArray(""));
    }

    @Test
    void banana() {
        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        assertArrayEquals(new int[]{0, 1, 3, 0, 0, 2},
                Kasai.build("banana", suffixArray).lcp());
    }

    @Test
    void emptyAndSingleCharacter() {
        assertArrayEquals(new int[0], Kasai.build("", new int[0]).lcp());
        assertArrayEquals(new int[]{0}, Kasai.build("a", new int[]{0}).lcp());
    }

    @Test
    void repeatedCharacters() {
        Kasai.Result result = Kasai.build("aaaa", new int[]{3, 2, 1, 0});
        assertArrayEquals(new int[]{0, 1, 2, 3}, result.lcp());
        assertEquals(3, result.comparisons());
    }

    @Test
    void invalidSuffixArray() {
        assertThrows(IllegalArgumentException.class,
                () -> Kasai.build("abc", new int[]{0, 1}));
        assertThrows(IllegalArgumentException.class,
                () -> Kasai.build("abc", new int[]{0, 0, 2}));
        assertThrows(IllegalArgumentException.class,
                () -> Kasai.build("abc", new int[]{0, 1, 3}));
        assertThrows(IllegalArgumentException.class,
                () -> Kasai.build("abc", new int[]{-1, 1, 2}));
    }

    @Test
    void randomCasesAgainstDirectCheck() {
        Random random = new Random(84);
        for (int test = 0; test < 100; test++) {
            int n = random.nextInt(60);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                builder.append("abcd".charAt(random.nextInt(4)));
            }
            String text = builder.toString();
            Integer[] order = new Integer[n];
            for (int i = 0; i < n; i++) {
                order[i] = i;
            }
            Arrays.sort(order, (a, b) -> text.substring(a).compareTo(text.substring(b)));
            int[] suffixArray = Arrays.stream(order).mapToInt(Integer::intValue).toArray();
            int[] expected = new int[n];
            for (int i = 1; i < n; i++) {
                int a = suffixArray[i - 1];
                int b = suffixArray[i];
                while (a + expected[i] < n && b + expected[i] < n
                        && text.charAt(a + expected[i]) == text.charAt(b + expected[i])) {
                    expected[i]++;
                }
            }
            assertArrayEquals(expected, Kasai.build(text, suffixArray).lcp());
        }
    }
}
