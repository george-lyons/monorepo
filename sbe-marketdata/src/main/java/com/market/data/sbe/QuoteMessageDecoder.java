/* Generated SBE (Simple Binary Encoding) message codec. */
package com.market.data.sbe;

import org.agrona.DirectBuffer;


/**
 * Market data quote
 */
@SuppressWarnings("all")
public final class QuoteMessageDecoder
{
    public static final int BLOCK_LENGTH = 45;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1.0";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final QuoteMessageDecoder parentMessage = this;
    private DirectBuffer buffer;
    private int initialOffset;
    private int offset;
    private int limit;
    int actingBlockLength;
    int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int initialOffset()
    {
        return initialOffset;
    }

    public int offset()
    {
        return offset;
    }

    public QuoteMessageDecoder wrap(
        final DirectBuffer buffer,
        final int offset,
        final int actingBlockLength,
        final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public QuoteMessageDecoder wrapAndApplyHeader(
        final DirectBuffer buffer,
        final int offset,
        final MessageHeaderDecoder headerDecoder)
    {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId)
        {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
            buffer,
            offset + MessageHeaderDecoder.ENCODED_LENGTH,
            headerDecoder.blockLength(),
            headerDecoder.version());
    }

    public QuoteMessageDecoder sbeRewind()
    {
        return wrap(buffer, initialOffset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        return decodedLength;
    }

    public int actingVersion()
    {
        return actingVersion;
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int exchangeTimestampNanosId()
    {
        return 1;
    }

    public static int exchangeTimestampNanosSinceVersion()
    {
        return 0;
    }

    public static int exchangeTimestampNanosEncodingOffset()
    {
        return 0;
    }

    public static int exchangeTimestampNanosEncodingLength()
    {
        return 8;
    }

    public static String exchangeTimestampNanosMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long exchangeTimestampNanosNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long exchangeTimestampNanosMinValue()
    {
        return 0x0L;
    }

    public static long exchangeTimestampNanosMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long exchangeTimestampNanos()
    {
        return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int receivedTimestampNanosId()
    {
        return 2;
    }

    public static int receivedTimestampNanosSinceVersion()
    {
        return 0;
    }

    public static int receivedTimestampNanosEncodingOffset()
    {
        return 8;
    }

    public static int receivedTimestampNanosEncodingLength()
    {
        return 8;
    }

    public static String receivedTimestampNanosMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long receivedTimestampNanosNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long receivedTimestampNanosMinValue()
    {
        return 0x0L;
    }

    public static long receivedTimestampNanosMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long receivedTimestampNanos()
    {
        return buffer.getLong(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int symbolId()
    {
        return 3;
    }

    public static int symbolSinceVersion()
    {
        return 0;
    }

    public static int symbolEncodingOffset()
    {
        return 16;
    }

    public static int symbolEncodingLength()
    {
        return 20;
    }

    public static String symbolMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static byte symbolNullValue()
    {
        return (byte)0;
    }

    public static byte symbolMinValue()
    {
        return (byte)32;
    }

    public static byte symbolMaxValue()
    {
        return (byte)126;
    }

    public static int symbolLength()
    {
        return 20;
    }


    public byte symbol(final int index)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 16 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String symbolCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public int getSymbol(final byte[] dst, final int dstOffset)
    {
        final int length = 20;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(offset + 16, dst, dstOffset, length);

        return length;
    }

    public String symbol()
    {
        final byte[] dst = new byte[20];
        buffer.getBytes(offset + 16, dst, 0, 20);

        int end = 0;
        for (; end < 20 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public int getSymbol(final Appendable value)
    {
        for (int i = 0; i < 20; ++i)
        {
            final int c = buffer.getByte(offset + 16 + i) & 0xFF;
            if (c == 0)
            {
                return i;
            }

            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException ex)
            {
                throw new java.io.UncheckedIOException(ex);
            }
        }

        return 20;
    }


    public static int sequenceNumberId()
    {
        return 4;
    }

    public static int sequenceNumberSinceVersion()
    {
        return 0;
    }

    public static int sequenceNumberEncodingOffset()
    {
        return 36;
    }

    public static int sequenceNumberEncodingLength()
    {
        return 8;
    }

    public static String sequenceNumberMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long sequenceNumberNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long sequenceNumberMinValue()
    {
        return 0x0L;
    }

    public static long sequenceNumberMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long sequenceNumber()
    {
        return buffer.getLong(offset + 36, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int exchangeIdId()
    {
        return 5;
    }

    public static int exchangeIdSinceVersion()
    {
        return 0;
    }

    public static int exchangeIdEncodingOffset()
    {
        return 44;
    }

    public static int exchangeIdEncodingLength()
    {
        return 1;
    }

    public static String exchangeIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static short exchangeIdNullValue()
    {
        return (short)255;
    }

    public static short exchangeIdMinValue()
    {
        return (short)0;
    }

    public static short exchangeIdMaxValue()
    {
        return (short)254;
    }

    public short exchangeId()
    {
        return ((short)(buffer.getByte(offset + 44) & 0xFF));
    }


    private final BidLevelsDecoder bidLevels = new BidLevelsDecoder(this);

    public static long bidLevelsDecoderId()
    {
        return 6;
    }

    public static int bidLevelsDecoderSinceVersion()
    {
        return 0;
    }

    public BidLevelsDecoder bidLevels()
    {
        bidLevels.wrap(buffer);
        return bidLevels;
    }

    public static final class BidLevelsDecoder
        implements Iterable<BidLevelsDecoder>, java.util.Iterator<BidLevelsDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final QuoteMessageDecoder parentMessage;
        private DirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        BidLevelsDecoder(final QuoteMessageDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final DirectBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            count = (buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }

        public BidLevelsDecoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static int countMinValue()
        {
            return 0;
        }

        public static int countMaxValue()
        {
            return 65534;
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 18;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<BidLevelsDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return index < count;
        }

        public static int priceId()
        {
            return 7;
        }

        public static int priceSinceVersion()
        {
            return 0;
        }

        public static int priceEncodingOffset()
        {
            return 0;
        }

        public static int priceEncodingLength()
        {
            return 9;
        }

        public static String priceMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private final Decimal64Decoder price = new Decimal64Decoder();

        public Decimal64Decoder price()
        {
            price.wrap(buffer, offset + 0);
            return price;
        }

        public static int quantityId()
        {
            return 8;
        }

        public static int quantitySinceVersion()
        {
            return 0;
        }

        public static int quantityEncodingOffset()
        {
            return 9;
        }

        public static int quantityEncodingLength()
        {
            return 9;
        }

        public static String quantityMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private final Decimal64Decoder quantity = new Decimal64Decoder();

        public Decimal64Decoder quantity()
        {
            quantity.wrap(buffer, offset + 9);
            return quantity;
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            if (null == buffer)
            {
                return builder;
            }

            builder.append('(');
            builder.append("price=");
            final Decimal64Decoder price = this.price();
            if (price != null)
            {
                price.appendTo(builder);
            }
            else
            {
                builder.append("null");
            }
            builder.append('|');
            builder.append("quantity=");
            final Decimal64Decoder quantity = this.quantity();
            if (quantity != null)
            {
                quantity.appendTo(builder);
            }
            else
            {
                builder.append("null");
            }
            builder.append(')');

            return builder;
        }
        
        public BidLevelsDecoder sbeSkip()
        {

            return this;
        }
    }

    private final AskLevelsDecoder askLevels = new AskLevelsDecoder(this);

    public static long askLevelsDecoderId()
    {
        return 9;
    }

    public static int askLevelsDecoderSinceVersion()
    {
        return 0;
    }

    public AskLevelsDecoder askLevels()
    {
        askLevels.wrap(buffer);
        return askLevels;
    }

    public static final class AskLevelsDecoder
        implements Iterable<AskLevelsDecoder>, java.util.Iterator<AskLevelsDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final QuoteMessageDecoder parentMessage;
        private DirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        AskLevelsDecoder(final QuoteMessageDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final DirectBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            count = (buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }

        public AskLevelsDecoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static int countMinValue()
        {
            return 0;
        }

        public static int countMaxValue()
        {
            return 65534;
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 18;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<AskLevelsDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return index < count;
        }

        public static int priceId()
        {
            return 10;
        }

        public static int priceSinceVersion()
        {
            return 0;
        }

        public static int priceEncodingOffset()
        {
            return 0;
        }

        public static int priceEncodingLength()
        {
            return 9;
        }

        public static String priceMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private final Decimal64Decoder price = new Decimal64Decoder();

        public Decimal64Decoder price()
        {
            price.wrap(buffer, offset + 0);
            return price;
        }

        public static int quantityId()
        {
            return 11;
        }

        public static int quantitySinceVersion()
        {
            return 0;
        }

        public static int quantityEncodingOffset()
        {
            return 9;
        }

        public static int quantityEncodingLength()
        {
            return 9;
        }

        public static String quantityMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private final Decimal64Decoder quantity = new Decimal64Decoder();

        public Decimal64Decoder quantity()
        {
            quantity.wrap(buffer, offset + 9);
            return quantity;
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            if (null == buffer)
            {
                return builder;
            }

            builder.append('(');
            builder.append("price=");
            final Decimal64Decoder price = this.price();
            if (price != null)
            {
                price.appendTo(builder);
            }
            else
            {
                builder.append("null");
            }
            builder.append('|');
            builder.append("quantity=");
            final Decimal64Decoder quantity = this.quantity();
            if (quantity != null)
            {
                quantity.appendTo(builder);
            }
            else
            {
                builder.append("null");
            }
            builder.append(')');

            return builder;
        }
        
        public AskLevelsDecoder sbeSkip()
        {

            return this;
        }
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final QuoteMessageDecoder decoder = new QuoteMessageDecoder();
        decoder.wrap(buffer, initialOffset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final int originalLimit = limit();
        limit(initialOffset + actingBlockLength);
        builder.append("[QuoteMessage](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("exchangeTimestampNanos=");
        builder.append(this.exchangeTimestampNanos());
        builder.append('|');
        builder.append("receivedTimestampNanos=");
        builder.append(this.receivedTimestampNanos());
        builder.append('|');
        builder.append("symbol=");
        for (int i = 0; i < symbolLength() && this.symbol(i) > 0; i++)
        {
            builder.append((char)this.symbol(i));
        }
        builder.append('|');
        builder.append("sequenceNumber=");
        builder.append(this.sequenceNumber());
        builder.append('|');
        builder.append("exchangeId=");
        builder.append(this.exchangeId());
        builder.append('|');
        builder.append("bidLevels=[");
        final int bidLevelsOriginalOffset = bidLevels.offset;
        final int bidLevelsOriginalIndex = bidLevels.index;
        final BidLevelsDecoder bidLevels = this.bidLevels();
        if (bidLevels.count() > 0)
        {
            while (bidLevels.hasNext())
            {
                bidLevels.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        bidLevels.offset = bidLevelsOriginalOffset;
        bidLevels.index = bidLevelsOriginalIndex;
        builder.append(']');
        builder.append('|');
        builder.append("askLevels=[");
        final int askLevelsOriginalOffset = askLevels.offset;
        final int askLevelsOriginalIndex = askLevels.index;
        final AskLevelsDecoder askLevels = this.askLevels();
        if (askLevels.count() > 0)
        {
            while (askLevels.hasNext())
            {
                askLevels.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        askLevels.offset = askLevelsOriginalOffset;
        askLevels.index = askLevelsOriginalIndex;
        builder.append(']');

        limit(originalLimit);

        return builder;
    }
    
    public QuoteMessageDecoder sbeSkip()
    {
        sbeRewind();
        BidLevelsDecoder bidLevels = this.bidLevels();
        if (bidLevels.count() > 0)
        {
            while (bidLevels.hasNext())
            {
                bidLevels.next();
                bidLevels.sbeSkip();
            }
        }
        AskLevelsDecoder askLevels = this.askLevels();
        if (askLevels.count() > 0)
        {
            while (askLevels.hasNext())
            {
                askLevels.next();
                askLevels.sbeSkip();
            }
        }

        return this;
    }
}
