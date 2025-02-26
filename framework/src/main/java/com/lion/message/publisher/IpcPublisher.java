package com.lion.message.publisher;

import com.lion.message.IntIdentifier;
import org.agrona.DirectBuffer;

public interface IpcPublisher<T extends IntIdentifier> {
    void publish(T msgType, DirectBuffer directBuffer, int offset, int length);
}
