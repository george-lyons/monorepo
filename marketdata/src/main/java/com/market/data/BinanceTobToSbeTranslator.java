package com.market.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.data.sbe.MessageHeaderEncoder;
import com.market.data.sbe.QuoteMessageEncoder;
import com.msg.ExchangeId;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

public class BinanceTobToSbeTranslator implements Translator{
    private static final Logger logger = LogManager.getLogger(BinanceTobToSbeTranslator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final short EXCHANGE_ID_BINANCE = ExchangeId.BINANCE.getId();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final QuoteMessageEncoder quoteEncoder = new QuoteMessageEncoder();
    private final UnsafeBuffer encodeBuffer;
    private final StringBuilder stringBuilder = new StringBuilder();

    public BinanceTobToSbeTranslator(int bufferSize) {
        this.encodeBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(bufferSize));
    }

    @Override
    public DirectBuffer translate(DirectBuffer sourceBuffer, int offset, int length) {
        try {
            // Parse JSON while reusing the same JsonNode objects
            JsonNode root = MAPPER.readTree(sourceBuffer.byteArray(), offset, length);

            // Initialize SBE message
            quoteEncoder.wrapAndApplyHeader(encodeBuffer, 0, headerEncoder);

            // Extract data from Binance bookTicker message
            // {
            //   "u":400900217,     // order book updateId
            //   "s":"BNBUSDT",     // symbol
            //   "b":"25.35190000", // best bid price
            //   "B":"31.21000000", // best bid qty
            //   "a":"25.36520000", // best ask price
            //   "A":"40.66000000"  // best ask qty
            // }

            String symbol = root.path("s").asText();
            long updateId = root.path("u").asLong();

            // Encode base fields
            quoteEncoder.timestamp(System.nanoTime()); // Use system time as message doesn't include time
            quoteEncoder.symbol(symbol);
            quoteEncoder.sequenceNumber(updateId);
            quoteEncoder.exchangeId(EXCHANGE_ID_BINANCE);

            // Encode single bid level
            QuoteMessageEncoder.BidLevelsEncoder bidLevels = quoteEncoder.bidLevelsCount(1);
            encodePriceLevel(bidLevels.next(),
                           root.path("b").asText(),
                           root.path("B").asText());

            // Encode single ask level
            QuoteMessageEncoder.AskLevelsEncoder askLevels = quoteEncoder.askLevelsCount(1);
            encodePriceLevel(askLevels.next(),
                           root.path("a").asText(),
                           root.path("A").asText());

            stringBuilder.setLength(0);
            quoteEncoder.appendTo(stringBuilder);

            if(logger.isDebugEnabled()) {
                logger.log(Level.DEBUG, stringBuilder.toString());
            }

            return encodeBuffer;
        } catch (Exception e) {
            logger.error("Failed to translate Binance message", e);
            return null;
        }
    }

    private void encodePriceLevel(QuoteMessageEncoder.BidLevelsEncoder encoder, String price, String qty) {
        // Parse price and quantity from strings to maintain precision
        double priceVal = Double.parseDouble(price);
        double qtyVal = Double.parseDouble(qty);

        // Convert to SBE decimal64 format
        long priceMantissa = (long)(priceVal * 1_000_000_000L); // 9 decimal places
        long qtyMantissa = (long)(qtyVal * 1_000_000_000L);

        encoder.price().mantissa(priceMantissa).exponent((byte)-9);
        encoder.quantity().mantissa(qtyMantissa).exponent((byte)-9);
    }

    private void encodePriceLevel(QuoteMessageEncoder.AskLevelsEncoder encoder, String price, String qty) {
        double priceVal = Double.parseDouble(price);
        double qtyVal = Double.parseDouble(qty);

        long priceMantissa = (long)(priceVal * 1_000_000_000L);
        long qtyMantissa = (long)(qtyVal * 1_000_000_000L);

        encoder.price().mantissa(priceMantissa).exponent((byte)-9);
        encoder.quantity().mantissa(qtyMantissa).exponent((byte)-9);
    }

    public int getEncodedLength() {
        return headerEncoder.encodedLength() + quoteEncoder.encodedLength();
    }
} 