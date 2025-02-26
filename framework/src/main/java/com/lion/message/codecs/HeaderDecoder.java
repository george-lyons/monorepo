package com.lion.message.codecs;

import com.lion.message.InternalMsgType;
import org.agrona.DirectBuffer;

public class HeaderDecoder {
    public static final int HEADER_LENGTH = HeaderEncoder.HEADER_LENGTH;
    
    private DirectBuffer buffer;
    private int offset;

    public HeaderDecoder wrap(DirectBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int messageLength() {
        return buffer.getInt(offset);
    }

    public InternalMsgType messageType() {
        return InternalMsgType.fromId(buffer.getInt(offset + 4));
    }

    public long receiveTimestampNanos() {
        return buffer.getLong(offset + 8);
    }
} 