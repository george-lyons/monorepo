package com.market.data;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public interface Translator {

    MutableDirectBuffer translate(DirectBuffer sourceBuffer, int offset, int length, long receivedNanoTime);

    int getEncodedLength();

}
