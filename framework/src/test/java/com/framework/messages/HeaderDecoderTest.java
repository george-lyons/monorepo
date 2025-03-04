package com.framework.messages;

import com.lion.message.GlobalMsgType;
import com.lion.message.codecs.HeaderDecoder;
import com.lion.message.codecs.HeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderDecoderTest {
    private static final int BUFFER_SIZE = 1024;
    private UnsafeBuffer buffer;
    private HeaderDecoder decoder;
    private HeaderEncoder encoder;

    @BeforeEach
    void setUp() {
        buffer = new UnsafeBuffer(new byte[BUFFER_SIZE]);
        decoder = new HeaderDecoder();
        encoder = new HeaderEncoder();
    }

    @Test
    void shouldDecodeMessageLength() {
        // Given
        int expectedLength = 100;
        encoder.wrap(buffer, 0).messageLength(expectedLength);

        // When
        decoder.wrap(buffer, 0);

        // Then
        assertEquals(expectedLength, decoder.messageLength());
    }

    @Test
    void shouldDecodeTimestampNanos() {
        encoder.wrap(buffer, 0).receiveTimestampNanos(10001L);

        // When
        decoder.wrap(buffer, 0);

        // Then
        assertEquals(10001L, decoder.receiveTimestampNanos());
    }

    @Test
    void shouldDecodeMessageType() {
        encoder.wrap(buffer, 0).messageType(GlobalMsgType.TOB_MARKET_DATA);

        // When
        decoder.wrap(buffer, 0);

        // Then
        assertEquals(GlobalMsgType.TOB_MARKET_DATA, decoder.messageType());
    }

    @Test
    void shouldDecodeHeaderAtOffset() {
        // Given
        int offset = 8;
        encoder.wrap(buffer, offset);
        encoder.messageLength(100);
        encoder.messageType(GlobalMsgType.TOB_MARKET_DATA);

        // When
        decoder.wrap(buffer, offset);

        // Then
        assertEquals(100, decoder.messageLength());
        assertEquals(GlobalMsgType.TOB_MARKET_DATA, decoder.messageType());
    }

    @Test
    void shouldDecodeHeaderMethods() {
        // Given
        int offset = 0;
        int messageLength = 200;

        encoder.wrap(buffer, offset);
        encoder.messageLength(messageLength);
        encoder.messageType(GlobalMsgType.TOB_MARKET_DATA);
        encoder.receiveTimestampNanos(1000L);

        decoder.wrap(buffer, offset);
        // When
        int decodedLength = decoder.messageLength();
        GlobalMsgType decodedType = decoder.messageType();
        long timestampNanos = decoder.receiveTimestampNanos();
        // Then
        assertEquals(messageLength, decodedLength);
        assertEquals(GlobalMsgType.TOB_MARKET_DATA, decodedType);
        assertEquals(1000L, timestampNanos);
    }

    @Test
    void shouldHandleMaxValues() {
        // Given
        encoder.wrap(buffer, 0);

        encoder.messageLength(Integer.MAX_VALUE);
        // When
        decoder.wrap(buffer, 0);

        // Then
        assertEquals(Integer.MAX_VALUE, decoder.messageLength());
    }
} 