package com.market.data;

import org.agrona.DirectBuffer;

public interface Translator {

    DirectBuffer translate(DirectBuffer sourceBuffer, int offset, int length, long receivedNanoTime);

    int getEncodedLength();

}
