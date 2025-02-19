/* Generated SBE (Simple Binary Encoding) message codec. */
package com.market.data.sbe;

import org.agrona.MutableDirectBuffer;


/**
 * Market data quote
 */
@SuppressWarnings("all")
public final class QuoteMessageEncoder
{
    public static final int BLOCK_LENGTH = 45;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1.0";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final QuoteMessageEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
    private int initialOffset;
    private int offset;
    private int limit;

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

    public MutableDirectBuffer buffer()
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

    public QuoteMessageEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public QuoteMessageEncoder wrapAndApplyHeader(
        final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
    {
        headerEncoder
            .wrap(buffer, offset)
            .blockLength(BLOCK_LENGTH)
            .templateId(TEMPLATE_ID)
            .schemaId(SCHEMA_ID)
            .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
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

    public QuoteMessageEncoder exchangeTimestampNanos(final long value)
    {
        buffer.putLong(offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public QuoteMessageEncoder receivedTimestampNanos(final long value)
    {
        buffer.putLong(offset + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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


    public QuoteMessageEncoder symbol(final int index, final byte value)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 16 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String symbolCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public QuoteMessageEncoder putSymbol(final byte[] src, final int srcOffset)
    {
        final int length = 20;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 16, src, srcOffset, length);

        return this;
    }

    public QuoteMessageEncoder symbol(final String src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 16, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 16 + start, (byte)0);
        }

        return this;
    }

    public QuoteMessageEncoder symbol(final CharSequence src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 16, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 16 + start, (byte)0);
        }

        return this;
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

    public QuoteMessageEncoder sequenceNumber(final long value)
    {
        buffer.putLong(offset + 36, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public QuoteMessageEncoder exchangeId(final short value)
    {
        buffer.putByte(offset + 44, (byte)value);
        return this;
    }


    private final BidLevelsEncoder bidLevels = new BidLevelsEncoder(this);

    public static long bidLevelsId()
    {
        return 6;
    }

    public BidLevelsEncoder bidLevelsCount(final int count)
    {
        bidLevels.wrap(buffer, count);
        return bidLevels;
    }

    public static final class BidLevelsEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final QuoteMessageEncoder parentMessage;
        private MutableDirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int initialLimit;

        BidLevelsEncoder(final QuoteMessageEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final MutableDirectBuffer buffer, final int count)
        {
            if (count < 0 || count > 65534)
            {
                throw new IllegalArgumentException("count outside allowed range: count=" + count);
            }

            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            this.count = count;
            final int limit = parentMessage.limit();
            initialLimit = limit;
            parentMessage.limit(limit + HEADER_SIZE);
            buffer.putShort(limit + 0, (short)18, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public BidLevelsEncoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + sbeBlockLength());
            ++index;

            return this;
        }

        public int resetCountToIndex()
        {
            count = index;
            buffer.putShort(initialLimit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);

            return count;
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

        private final Decimal64Encoder price = new Decimal64Encoder();

        public Decimal64Encoder price()
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

        private final Decimal64Encoder quantity = new Decimal64Encoder();

        public Decimal64Encoder quantity()
        {
            quantity.wrap(buffer, offset + 9);
            return quantity;
        }
    }

    private final AskLevelsEncoder askLevels = new AskLevelsEncoder(this);

    public static long askLevelsId()
    {
        return 9;
    }

    public AskLevelsEncoder askLevelsCount(final int count)
    {
        askLevels.wrap(buffer, count);
        return askLevels;
    }

    public static final class AskLevelsEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final QuoteMessageEncoder parentMessage;
        private MutableDirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int initialLimit;

        AskLevelsEncoder(final QuoteMessageEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final MutableDirectBuffer buffer, final int count)
        {
            if (count < 0 || count > 65534)
            {
                throw new IllegalArgumentException("count outside allowed range: count=" + count);
            }

            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            this.count = count;
            final int limit = parentMessage.limit();
            initialLimit = limit;
            parentMessage.limit(limit + HEADER_SIZE);
            buffer.putShort(limit + 0, (short)18, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public AskLevelsEncoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + sbeBlockLength());
            ++index;

            return this;
        }

        public int resetCountToIndex()
        {
            count = index;
            buffer.putShort(initialLimit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);

            return count;
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

        private final Decimal64Encoder price = new Decimal64Encoder();

        public Decimal64Encoder price()
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

        private final Decimal64Encoder quantity = new Decimal64Encoder();

        public Decimal64Encoder quantity()
        {
            quantity.wrap(buffer, offset + 9);
            return quantity;
        }
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final QuoteMessageDecoder decoder = new QuoteMessageDecoder();
        decoder.wrap(buffer, initialOffset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }
}
