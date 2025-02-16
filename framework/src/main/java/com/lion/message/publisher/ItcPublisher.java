package com.lion.message.publisher;

import org.agrona.DirectBuffer;

public interface ItcPublisher {
    void publish(int msgType, DirectBuffer directBuffer, int offset, int length);
}
