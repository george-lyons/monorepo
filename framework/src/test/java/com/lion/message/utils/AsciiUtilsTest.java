package com.lion.message.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.lion.message.utils.AsciiUtils.parseDoubleAscii;
import static org.junit.jupiter.api.Assertions.*;

class AsciiUtilsTest {
    private static final Random RANDOM = new Random();

    @Test
    void testParseDoubleAscii_ValidCases() {
        // ✅ Manually verified cases
        assertEquals(123.456, parseDoubleAscii("123.456".toCharArray(), 0, 7), 1e-9);
        assertEquals(0.001, parseDoubleAscii("0.001".toCharArray(), 0, 5), 1e-9);
        assertEquals(98765.4321, parseDoubleAscii("98765.4321".toCharArray(), 0, 10), 1e-9);
        assertEquals(1.0, parseDoubleAscii("1.0".toCharArray(), 0, 3), 1e-9);
        assertEquals(999999.999999, parseDoubleAscii("999999.999999".toCharArray(), 0, 13), 1e-9);
    }

    @Test
    void testParseDoubleAscii_RandomCases() {
        for (int i = 0; i < 500; i++) { // ✅ Run 500 randomized cases
            double expected = generateRandomDouble();
            String str = String.format("%.9f", expected); // 9 decimal places precision
            double parsed = parseDoubleAscii(str.toCharArray(), 0, str.length());
            assertEquals(expected, parsed, 1e-9, "Failed for input: " + str);
        }
    }

    @Test
    void testParseDoubleAscii_InvalidCases() {
        // ✅ Invalid characters
        assertThrows(NumberFormatException.class, () ->
                parseDoubleAscii("12a.45".toCharArray(), 0, 6));

        assertThrows(NumberFormatException.class, () ->
                parseDoubleAscii("98.76.54".toCharArray(), 0, 8)); // Multiple dots

        assertThrows(NumberFormatException.class, () ->
                parseDoubleAscii("-123.45".toCharArray(), 0, 7)); // Negative numbers not supported
    }

    /**
     * Generates a random double with up to 9 decimal places.
     */
    private double generateRandomDouble() {
        double base = RANDOM.nextDouble() * 1_000_000; // Random number up to 1,000,000
        return Math.round(base * 1_000_000_000L) / 1_000_000_000.0; // Ensure 9 decimal places
    }
}