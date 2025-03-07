package com.handler;

import com.lion.message.GlobalMsgType;
import com.lion.message.publisher.ItcPublisher;
import com.market.data.sbe.MessageHeaderDecoder;
import com.market.data.sbe.QuoteMessageDecoder;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteEventHandlerLog implements ItcPublisher<GlobalMsgType> {
    private static final Logger logger = LoggerFactory.getLogger(QuoteEventHandlerLog.class);
    private final StringBuilder messageContainer = new StringBuilder();
    private final QuoteMessageDecoder quoteMessageDecoder = new QuoteMessageDecoder();
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    @Override
    public void publish(GlobalMsgType msgType, DirectBuffer directBuffer, int offset, int length) {
        messageContainer.setLength(0);
        quoteMessageDecoder.wrapAndApplyHeader(directBuffer, offset, messageHeaderDecoder);
        messageContainer.append(quoteMessageDecoder);
        logger.info(messageContainer.toString());// fix to string
    }
}
