package com.execution;

import com.market.data.sbe.QuoteMessageDecoder;
import com.msg.ExecutionEngineMsgType;

public interface ExecutionTask {
    /**
     * Handles an event with the given message buffer.
     *
     * @param messageType the type of message
     * @param buffer the message buffer
     * @param offset the offset in the buffer
     * @param length the length of the message
     */
//    void handleEvent(ExecutionEngineMsgType messageType, DirectBuffer buffer, int offset, int length);


    /**
     * Handles an event with the given message buffer.
     *
     * @param messageType the type of message
     * @param quoteMessageDecoder the sbe message
     */
    void handleMarketData(ExecutionEngineMsgType messageType, QuoteMessageDecoder quoteMessageDecoder);

    /**
     * Checks if the execution task is available to process messages.
     *
     * @return true if available, false otherwise.
     */
    boolean isAvailable();
}