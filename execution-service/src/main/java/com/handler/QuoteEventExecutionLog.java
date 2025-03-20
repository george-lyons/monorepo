package com.handler;

import com.execution.ExecutionTask;
import com.market.data.sbe.QuoteMessageDecoder;
import com.msg.ExecutionEngineMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteEventExecutionLog implements ExecutionTask {
    private static final Logger logger = LoggerFactory.getLogger(QuoteEventExecutionLog.class);
    private final StringBuilder messageContainer = new StringBuilder();
    @Override
    public void handleMarketData(ExecutionEngineMsgType messageType, QuoteMessageDecoder quoteMessageDecoder) {
        messageContainer.setLength(0);
        messageContainer.append(quoteMessageDecoder);
        logger.info(messageContainer.toString());// fix to string
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
