package kz.aitu.daa.assignment4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicProgrammingTest {
    @Test
    void lcsIdentical() {
        assertEquals(4, DynamicProgramming.lcs("java", "java"));
    }

    @Test
    void lcsDifferent() {
        assertEquals(0, DynamicProgramming.lcs("abc", "xyz"));
    }

    @Test
    void lcsEmpty() {
        assertEquals(0, DynamicProgramming.lcs("", "abc"));
        assertEquals(0, DynamicProgramming.lcs("abc", ""));
        assertEquals(0, DynamicProgramming.lcs("", ""));
    }

    @Test
    void lcsNonContiguous() {
        assertEquals(3, DynamicProgramming.lcs("abcde", "ace"));
        assertEquals(4, DynamicProgramming.lcs("AGGTAB", "GXTXAYB"));
    }

    @Test
    void lcsRepeatedCharacters() {
        assertEquals(2, DynamicProgramming.lcs("aaaa", "aa"));
    }

    @Test
    void editIdentical() {
        assertEquals(0, DynamicProgramming.editDistance("java", "java"));
    }

    @Test
    void editEmpty() {
        assertEquals(3, DynamicProgramming.editDistance("", "abc"));
        assertEquals(3, DynamicProgramming.editDistance("abc", ""));
        assertEquals(0, DynamicProgramming.editDistance("", ""));
    }

    @Test
    void editSubstitution() {
        assertEquals(1, DynamicProgramming.editDistance("cat", "cut"));
    }

    @Test
    void editInsertion() {
        assertEquals(1, DynamicProgramming.editDistance("cat", "cart"));
    }

    @Test
    void editDeletion() {
        assertEquals(1, DynamicProgramming.editDistance("cart", "cat"));
    }

    @Test
    void editCombinedOperations() {
        assertEquals(3, DynamicProgramming.editDistance("kitten", "sitting"));
        assertEquals(2, DynamicProgramming.editDistance("ab", "ba"));
    }
}
