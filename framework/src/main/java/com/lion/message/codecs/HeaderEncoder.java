package com.lion.message.codecs;

import com.lion.message.FrameworkMsg;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class HeaderEncoder {
    public static final int HEADER_LENGTH = 16;  // 4 bytes for length + 4 bytes for message type 8 bytes for nanos time
    
    private MutableDirectBuffer buffer;
    private int offset;

    public HeaderEncoder() {
        this.buffer = new UnsafeBuffer(new byte[HEADER_LENGTH]);
    }

    public HeaderEncoder wrap(MutableDirectBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public void messageLength(int length) {
        buffer.putInt(offset, length);
    }

    public void messageType(int msgType) {
        buffer.putInt(offset + 4, msgType);
    }

    public void messageType(FrameworkMsg messageType) {
        buffer.putInt(offset + 4, messageType.getId());
    }

    public void receiveTimestampNanos(long receiveTimestampNanos) {
        buffer.putLong(offset + 8, receiveTimestampNanos);
    }

    //TODO seq num

    public int length() {
        return HEADER_LENGTH;
    }

} 