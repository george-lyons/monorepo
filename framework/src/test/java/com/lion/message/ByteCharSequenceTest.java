package com.lion.message;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteCharSequenceTest {

    @Test
    void testConstructor_Default() {
        ByteCharSequence bcs = new ByteCharSequence();
        assertEquals(0, bcs.length());
        assertNotNull(bcs.getBytes());
    }

    @Test
    void testConstructor_InitialSize() {
        ByteCharSequence bcs = new ByteCharSequence(32);
        assertEquals(0, bcs.length());
    }

    @Test
    void testConstructor_Copy() {
        ByteCharSequence original = new ByteCharSequence();
        original.append("TestCopy");
        ByteCharSequence copy = new ByteCharSequence(original);
        assertEquals("TestCopy", copy.toString());
        assertEquals(original.length(), copy.length());
    }

    @Test
    void testReset() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("ResetTest");
        assertEquals(9, bcs.length());
        bcs.reset();
        assertEquals(0, bcs.length());
    }

    @Test
    void testFrom_ByteArray() {
        ByteCharSequence bcs = new ByteCharSequence();
        byte[] data = "Hello".getBytes();
        bcs.from(data, 0, data.length);
        assertEquals("Hello", bcs.toString());
        assertEquals(5, bcs.length());
    }

    @Test
    void testFrom_DirectBuffer() {
        ByteCharSequence bcs = new ByteCharSequence();
        byte[] data = "DirectBuffer".getBytes();
        DirectBuffer buffer = new UnsafeBuffer(data);
        bcs.from(buffer, 0, data.length);
        assertEquals("DirectBuffer", bcs.toString());
    }

    @Test
    void testFromBytesContainingTrailingNull() {
        ByteCharSequence bcs = new ByteCharSequence();
        byte[] data = {'A', 'B', 'C', 0, 0, 0}; // Should only keep "ABC"
        bcs.fromBytesContainingTrailingNull(data, data.length);
        assertEquals("ABC", bcs.toString());
    }

    @Test
    void testCharAt_ValidIndex() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("Hello");
        assertEquals('H', bcs.charAt(0));
        assertEquals('o', bcs.charAt(4));
    }

    @Test
    void testCharAt_IndexOutOfBounds() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("Test");
        assertThrows(IndexOutOfBoundsException.class, () -> bcs.charAt(10));
    }

    @Test
    void testSubSequence_ValidRange() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("HelloWorld");
        CharSequence sub = bcs.subSequence(0, 5);
        assertEquals("Hello", sub.toString());
    }

    @Test
    void testSubSequence_InvalidRange() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("Data");
        assertThrows(IndexOutOfBoundsException.class, () -> bcs.subSequence(2, 10));
    }

    @Test
    void testAppend_CharSequence() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("Hello");
        assertEquals("Hello", bcs.toString());
    }

    @Test
    void testAppend_Char() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append('A');
        assertEquals("A", bcs.toString());
    }

    @Test
    void testAppend_SubSequence() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("HelloWorld", 5, 10);
        assertEquals("World", bcs.toString());
    }

    @Test
    void testGetBytes() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("TestBytes");
        byte[] bytes = bcs.getBytes();
        assertEquals("TestBytes", new String(bytes));
    }

    @Test
    void testClear() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("ClearMe");
        bcs.clear();
        assertEquals(0, bcs.length());
    }

    @Test
    void testToString() {
        ByteCharSequence bcs = new ByteCharSequence();
        bcs.append("StringTest");
        assertEquals("StringTest", bcs.toString());
    }
}