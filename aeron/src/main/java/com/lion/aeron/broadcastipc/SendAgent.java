/*
 * Copyright 2019-2023 Adaptive Financial Consulting Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lion.aeron.broadcastipc;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.broadcast.BroadcastTransmitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ReusableMessageFactory;

public class SendAgent implements Agent
{
    private final ShutdownSignalBarrier barrier;
    private final int sendCount;
    private final BroadcastTransmitter transmitter;
    private int lastSend;
    private boolean completed;
    private static final Logger logger = LogManager.getLogger(SendAgent.class, ReusableMessageFactory.INSTANCE);
    private final MutableDirectBuffer msgBuffer = new ExpandableArrayBuffer();

    public SendAgent(final AtomicBuffer buffer, final ShutdownSignalBarrier barrier, final int sendCount)
    {
        this.barrier = barrier;
        this.sendCount = sendCount;
        this.transmitter = new BroadcastTransmitter(buffer);
        this.lastSend = 0;
        this.completed = false;
    }

    @Override
    public int doWork()
    {
        if (completed)
        {
            return 0;
        }

        if (lastSend == sendCount)
        {
            logger.info("completed send: {}", lastSend);
            barrier.signal();
            completed = true;
            return 0;
        }

        msgBuffer.putInt(0, lastSend);
        transmitter.transmit(1, msgBuffer, 0, Integer.BYTES);

        lastSend++;

        return 0;
    }

    @Override
    public String roleName()
    {
        return "sender";
    }
}