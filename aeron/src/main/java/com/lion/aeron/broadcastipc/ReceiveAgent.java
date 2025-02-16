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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.broadcast.BroadcastReceiver;
import org.agrona.concurrent.broadcast.CopyBroadcastReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ReusableMessageFactory;

public class ReceiveAgent implements Agent, MessageHandler
{
    private static final Logger LOGGER = LogManager.getLogger(ReceiveAgent.class, ReusableMessageFactory.INSTANCE);

    private final BroadcastReceiver broadcastReceiver;
    private final String name;
    private final CopyBroadcastReceiver copyBroadcastReceiver;

    public ReceiveAgent(final AtomicBuffer atomicBuffer, final String name)
    {
        this.broadcastReceiver = new BroadcastReceiver(atomicBuffer);
        this.name = name;
        this.copyBroadcastReceiver = new CopyBroadcastReceiver(broadcastReceiver);
    }

    @Override
    public int doWork()
    {
        copyBroadcastReceiver.receive(this::onMessage);
        return 0;
    }

    @Override
    public String roleName()
    {
        return name;
    }

    @Override
    public void onMessage(final int msgTypeId, final MutableDirectBuffer buffer, final int index, final int length)
    {
        LOGGER.info("Received {}", buffer.getInt(index));
    }
}