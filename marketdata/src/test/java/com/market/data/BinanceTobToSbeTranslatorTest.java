package com.market.data;

import com.market.data.sbe.MessageHeaderDecoder;
import com.market.data.sbe.QuoteMessageDecoder;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BinanceTobToSbeTranslatorTest {
    private static final String BINANCE_BOOK_TICKER = """
            {
                "u": 400900217,
                "s": "BTCUSDT",
                "b": "43123.45000000",
                "B": "1.23400000",
                "a": "43123.46000000",
                "A": "0.89700000"
            }""";

    private BinanceTobToSbeTranslator translator;
    private UnsafeBuffer sourceBuffer;
    private MessageHeaderDecoder headerDecoder;
    private QuoteMessageDecoder quoteDecoder;

    @BeforeEach
    void setUp() {
        translator = new BinanceTobToSbeTranslator(1024);
        byte[] bytes = BINANCE_BOOK_TICKER.getBytes(StandardCharsets.UTF_8);
        sourceBuffer = new UnsafeBuffer(bytes);
        headerDecoder = new MessageHeaderDecoder();
        quoteDecoder = new QuoteMessageDecoder();
    }

    @Test
    void shouldTranslateBookTickerMessage() {
        // When
        DirectBuffer result = translator.translate(sourceBuffer, 0, sourceBuffer.capacity(), 1001L);

        // Then
        assertNotNull(result);
        
        // Decode the message
        headerDecoder.wrap(result, 0);
        assertEquals(1, headerDecoder.schemaId());
        
        quoteDecoder.wrap(result, 
                         MessageHeaderDecoder.ENCODED_LENGTH,
                         headerDecoder.blockLength(),
                         headerDecoder.version());

        // Verify fields
        assertEquals(400900217, quoteDecoder.sequenceNumber());
        assertEquals("BTCUSDT", quoteDecoder.symbol());
        assertEquals(1, quoteDecoder.exchangeId()); // BINANCE
        assertEquals(1001L, quoteDecoder.exchangeTimestampNanos());
        assertEquals(1001L, quoteDecoder.exchangeTimestampNanos());

        // Verify bid
        QuoteMessageDecoder.BidLevelsDecoder bidLevels = quoteDecoder.bidLevels();
        assertTrue(bidLevels.hasNext());
        bidLevels.next();
        assertEquals(43123450000000L, bidLevels.price().mantissa());
        assertEquals(-9, bidLevels.price().exponent());
        assertEquals(1234000000L, bidLevels.quantity().mantissa());
        assertEquals(-9, bidLevels.quantity().exponent());
        assertFalse(bidLevels.hasNext());

        // Verify ask
        QuoteMessageDecoder.AskLevelsDecoder askLevels = quoteDecoder.askLevels();
        assertTrue(askLevels.hasNext());
        askLevels.next();
        assertEquals(43123460000000L, askLevels.price().mantissa());
        assertEquals(-9, askLevels.price().exponent());
        assertEquals(897000000L, askLevels.quantity().mantissa());
        assertEquals(-9, askLevels.quantity().exponent());
        assertFalse(askLevels.hasNext());
    }

    @Test
    void shouldHandleInvalidJson() {
        // Given
        byte[] invalidJson = "invalid json".getBytes(StandardCharsets.UTF_8);
        sourceBuffer.wrap(invalidJson);

        // When
        DirectBuffer result = translator.translate(sourceBuffer, 0, sourceBuffer.capacity(), 1001L);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleMissingFields() {
        // Given
        String incompleteJson = """
            {
                "u": 400900217,
                "s": "BTCUSDT",
                "b": "43123.45000000"
            }""";
        sourceBuffer.wrap(incompleteJson.getBytes(StandardCharsets.UTF_8));

        // When
        DirectBuffer result = translator.translate(sourceBuffer, 0, sourceBuffer.capacity(), 1001L);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleInvalidNumbers() {
        // Given
        String invalidNumberJson = """
            {
                "u": 400900217,
                "s": "BTCUSDT",
                "b": "invalid",
                "B": "1.23400000",
                "a": "43123.46000000",
                "A": "0.89700000"
            }""";
        sourceBuffer.wrap(invalidNumberJson.getBytes(StandardCharsets.UTF_8));

        // When
        DirectBuffer result = translator.translate(sourceBuffer, 0, sourceBuffer.capacity(), 1001L);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleOffsetAndLength() {
        // Given
        byte[] paddedMessage = ("PADDING" + BINANCE_BOOK_TICKER + "MORE_PADDING")
            .getBytes(StandardCharsets.UTF_8);
        sourceBuffer.wrap(paddedMessage);
        int offset = 7; // length of "PADDING"
        int length = BINANCE_BOOK_TICKER.length();

        // When
        DirectBuffer result = translator.translate(sourceBuffer, offset, length, 1001L);

        // Then
        assertNotNull(result);
        
        // Verify message can be decoded
        headerDecoder.wrap(result, 0);
        quoteDecoder.wrap(result, 
                         MessageHeaderDecoder.ENCODED_LENGTH,
                         headerDecoder.blockLength(),
                         headerDecoder.version());
        
        assertEquals(400900217, quoteDecoder.sequenceNumber());
        assertEquals("BTCUSDT", quoteDecoder.symbol());
    }

    @Test
    void shouldMaintainExactDecimalPrecision() {
        // Given
        String preciseJson = """
            {
                "u": 400900217,
                "s": "BTCUSDT",
                "b": "43123.45678912",
                "B": "1.23456789",
                "a": "43123.89876543",
                "A": "0.98765432"
            }""";
        sourceBuffer.wrap(preciseJson.getBytes(StandardCharsets.UTF_8));

        // When
        DirectBuffer result = translator.translate(sourceBuffer, 0, sourceBuffer.capacity(), 1001L);

        // Then
        assertNotNull(result);
        headerDecoder.wrap(result, 0);
        quoteDecoder.wrap(result, 
                         MessageHeaderDecoder.ENCODED_LENGTH,
                         headerDecoder.blockLength(),
                         headerDecoder.version());

        // Verify bid
        QuoteMessageDecoder.BidLevelsDecoder bidLevels = quoteDecoder.bidLevels();
        assertTrue(bidLevels.hasNext());
        bidLevels.next();
        
        // Check bid price 43123.45678912
        long bidPriceMantissa = bidLevels.price().mantissa();
        int bidPriceExponent = bidLevels.price().exponent();
        double reconstructedBidPrice = bidPriceMantissa * Math.pow(10, bidPriceExponent);
        assertEquals(43123.45678912, reconstructedBidPrice, 0.00000001);
        
        // Check bid quantity 1.23456789
        long bidQtyMantissa = bidLevels.quantity().mantissa();
        int bidQtyExponent = bidLevels.quantity().exponent();
        double reconstructedBidQty = bidQtyMantissa * Math.pow(10, bidQtyExponent);
        assertEquals(1.23456789, reconstructedBidQty, 0.00000001);

        // Verify ask
        QuoteMessageDecoder.AskLevelsDecoder askLevels = quoteDecoder.askLevels();
        assertTrue(askLevels.hasNext());
        askLevels.next();
        
        // Check ask price 43123.89876543
        long askPriceMantissa = askLevels.price().mantissa();
        int askPriceExponent = askLevels.price().exponent();
        double reconstructedAskPrice = askPriceMantissa * Math.pow(10, askPriceExponent);
        assertEquals(43123.89876543, reconstructedAskPrice, 0.00000001);
        
        // Check ask quantity 0.98765432
        long askQtyMantissa = askLevels.quantity().mantissa();
        int askQtyExponent = askLevels.quantity().exponent();
        double reconstructedAskQty = askQtyMantissa * Math.pow(10, askQtyExponent);
        assertEquals(0.98765432, reconstructedAskQty, 0.00000001);

        // Print actual values for verification
        System.out.printf("Bid Price: %f (mantissa=%d, exponent=%d)%n", 
            reconstructedBidPrice, bidPriceMantissa, bidPriceExponent);
        System.out.printf("Bid Qty: %f (mantissa=%d, exponent=%d)%n", 
            reconstructedBidQty, bidQtyMantissa, bidQtyExponent);
        System.out.printf("Ask Price: %f (mantissa=%d, exponent=%d)%n", 
            reconstructedAskPrice, askPriceMantissa, askPriceExponent);
        System.out.printf("Ask Qty: %f (mantissa=%d, exponent=%d)%n", 
            reconstructedAskQty, askQtyMantissa, askQtyExponent);
    }

} 