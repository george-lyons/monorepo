package com.lion.clock;

public class SystemClock implements Clock {
    @Override
    public long getTimeNanos() {
        return System.nanoTime();
    }
}
