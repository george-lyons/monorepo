package com.lion.message.utils;

public class AsciiUtils {

    /**
     * TODO reusable method
     * @param chars
     * @param offset
     * @param length
     * @return
     */
    public static double parseDoubleAscii(char[] chars, int offset, int length) {
        double result = 0;
        double factor = 1;
        boolean isFractional = false;
        boolean hasFractionalPart = false; // Track if a decimal point is followed by digits

        for (int i = offset; i < offset + length; i++) {
            char c = chars[i];

            if (c == '.') {
                if (isFractional) { // More than one decimal point
                    throw new NumberFormatException("Invalid number format: multiple decimal points in " + new String(chars, offset, length));
                }
                isFractional = true;
                continue;
            }

            if (c < '0' || c > '9') {
                throw new NumberFormatException("Invalid digit '" + c + "' in input: " + new String(chars, offset, length));
            }

            int digit = c - '0';
            if (isFractional) {
                factor /= 10;
                result += digit * factor;
                hasFractionalPart = true;
            } else {
                result = result * 10 + digit;
            }
        }

        if (isFractional && !hasFractionalPart) {
            throw new NumberFormatException("Invalid number format: decimal point without fraction in " + new String(chars, offset, length));
        }

        return result;
    }
}
