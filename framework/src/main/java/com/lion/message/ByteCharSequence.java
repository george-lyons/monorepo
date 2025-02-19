package com.lion.message;

import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;

/**
 * A reusable and mutable string-like structure that minimizes object creation.
 * This class does not wrap an existing buffer but creates its own copy.
 *
 * **Not thread-safe** – should be used in single-threaded environments.
 */
public class ByteCharSequence implements CharSequence, Appendable {
    protected final ExpandableArrayBuffer buffer;
    protected int length;

    public ByteCharSequence() {
        this(16);  // Default initial capacity
    }

    public ByteCharSequence(int initialSize) {
        buffer = new ExpandableArrayBuffer(initialSize);
        reset();
    }

    public ByteCharSequence(ByteCharSequence other) {
        this.length = other.length;
        this.buffer = new ExpandableArrayBuffer(other.length);
        buffer.putBytes(0, other.buffer, 0, other.length);
    }

    public void reset() {
        length = 0;
    }

    public ByteCharSequence clear() {
        reset();
        return this;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[length];
        buffer.getBytes(0, bytes, 0, length);
        return bytes;
    }

    public int from(DirectBuffer src, int offset, int length) {
        buffer.putBytes(0, src, offset, length);
        this.length = length;
        return length;
    }

    public int from(byte[] src, int offset, int length) {
        buffer.putBytes(0, src, offset, length);
        this.length = length;
        return length;
    }

    /**
     * Handles byte arrays that may contain trailing null characters.
     */
    public int fromBytesContainingTrailingNull(byte[] src, int length) {
        int actualLength = length;
        for (int i = 0; i < length; i++) {
            if (src[i] == 0) {
                actualLength = i;
                break;
            }
        }
        return from(src, 0, actualLength);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + length);
        }
        return (char) buffer.getByte(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > length || start > end) {
            throw new IndexOutOfBoundsException("Invalid subsequence range: " + start + " to " + end);
        }
        ByteCharSequence subSeq = new ByteCharSequence(end - start);
        subSeq.from(buffer.byteArray(), start, end - start);
        return subSeq;
    }

    @Override
    public ByteCharSequence append(CharSequence csq) {
        if (csq == null) {
            return this;
        }
        byte[] bytes = csq.toString().getBytes();
        buffer.putBytes(length, bytes, 0, bytes.length);
        length += bytes.length;
        return this;
    }

    @Override
    public ByteCharSequence append(char c) {
        buffer.putByte(length, (byte) c);
        length++;
        return this;
    }

    @Override
    public ByteCharSequence append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    @Override
    public String toString() {
        return new String(getBytes());
    }
}