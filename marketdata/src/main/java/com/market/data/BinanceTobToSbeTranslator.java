package com.market.data;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.lion.message.ByteCharSequence;
import com.market.data.sbe.MessageHeaderEncoder;
import com.market.data.sbe.QuoteMessageEncoder;
import com.msg.ExchangeId;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.lion.message.utils.AsciiUtils.parseDoubleAscii;

public class BinanceTobToSbeTranslator implements Translator {
    private static final Logger logger = LogManager.getLogger(BinanceTobToSbeTranslator.class);

    private static final short EXCHANGE_ID_BINANCE = ExchangeId.BINANCE.getId();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final QuoteMessageEncoder quoteEncoder = new QuoteMessageEncoder();
    private final UnsafeBuffer encodeBuffer;
    private final JsonFactory jsonFactory = new JsonFactory();
    private final StringBuilder stringBuilder = new StringBuilder();
    private final ByteCharSequence symbol = new ByteCharSequence();

    public BinanceTobToSbeTranslator(int bufferSize) {
        this.encodeBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(bufferSize));
    }

    private void copyJsonTextToStringBuilder(JsonParser parser, ByteCharSequence sb) throws IOException {
        symbol.reset();
        char[] chars = parser.getTextCharacters();
        int offset = parser.getTextOffset();
        int length = parser.getTextLength();
        // Write char[] directly into UnsafeBuffer as bytes
        for (int i = 0; i < length; i++) {
            sb.append(chars[offset + i]); // Convert byte to char and append
        }
    }


    private double jsonTextCharsToDOuble(JsonParser parser) throws IOException {
        char[] chars = parser.getTextCharacters();
        int offset = parser.getTextOffset();
        int length = parser.getTextLength();
        return parseDoubleAscii(chars, offset, length); // ✅ Zero-alloc parsing
    }

    @Override
    public DirectBuffer translate(DirectBuffer sourceBuffer, int offset, int length, long receivedNanoTime) {
        double bidPrice = 0;
        double offerPrice= 0;
        double bidQty = 0;
        double offerQty= 0;

        try (JsonParser parser = jsonFactory.createParser(sourceBuffer.byteArray(), offset, length)) {
            long updateId = 0;

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = parser.getCurrentName();
                    parser.nextToken(); // Move to value


                    switch (fieldName) {
                        case "u" -> updateId = parser.getLongValue();
                        case "s" -> copyJsonTextToStringBuilder(parser, symbol);
                        case "b" -> bidPrice = jsonTextCharsToDOuble(parser);
                        case "B" -> bidQty = jsonTextCharsToDOuble(parser);
                        case "a" -> offerPrice = jsonTextCharsToDOuble(parser);
                        case "A" -> offerQty = jsonTextCharsToDOuble(parser);}
                }
            }

            if (!(bidPrice > 0)|| !(offerPrice >0)) return null; // Invalid JSON
            // Encode message
            quoteEncoder.wrapAndApplyHeader(encodeBuffer, 0, headerEncoder);
            quoteEncoder.sequenceNumber(updateId);
            quoteEncoder.exchangeId(EXCHANGE_ID_BINANCE);
            quoteEncoder.symbol(symbol); //TODO symbol to a primtive value mapped
            quoteEncoder.receivedTimestampNanos(receivedNanoTime);
            quoteEncoder.exchangeTimestampNanos(-1); //TODO

            // Encode bid level
            QuoteMessageEncoder.BidLevelsEncoder bidLevels = quoteEncoder.bidLevelsCount(1);
            encodePriceLevel(bidLevels.next(), bidPrice, bidQty);

            // Encode ask level
            QuoteMessageEncoder.AskLevelsEncoder askLevels = quoteEncoder.askLevelsCount(1);
            encodePriceLevel(askLevels.next(), offerPrice, offerQty);

            stringBuilder.setLength(0);
            quoteEncoder.appendTo(stringBuilder);

            if(logger.isDebugEnabled()) {
                logger.log(Level.DEBUG, stringBuilder.toString());
            }

            return encodeBuffer;
        } catch (Exception e) {
            e.printStackTrace();
            //TODO handle gracefully
            return null;
        }
    }

    private void encodePriceLevel(QuoteMessageEncoder.BidLevelsEncoder encoder, double price, double qty) {
        // Convert to SBE decimal64 format
        long priceMantissa = (long)(price * 1_000_000_000L); // 9 decimal places
        long qtyMantissa = (long)(qty * 1_000_000_000L);

        encoder.price().mantissa(priceMantissa).exponent((byte)-9);
        encoder.quantity().mantissa(qtyMantissa).exponent((byte)-9);
    }

    private void encodePriceLevel(QuoteMessageEncoder.AskLevelsEncoder encoder, double price, double qty) {
        // Convert to SBE decimal64 format
        long priceMantissa = (long)(price * 1_000_000_000L); // 9 decimal places
        long qtyMantissa = (long)(qty * 1_000_000_000L);

        encoder.price().mantissa(priceMantissa).exponent((byte)-9);
        encoder.quantity().mantissa(qtyMantissa).exponent((byte)-9);
    }

    @Override
    public int getEncodedLength() {
        return headerEncoder.encodedLength() + quoteEncoder.encodedLength();
    }

}