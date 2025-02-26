package com.lion.ringbuffer;

import com.lion.message.IntIdentifier;
import com.lion.message.InternalMsgType;
import com.lion.message.publisher.IpcPublisher;
import org.agrona.DirectBuffer;
import org.agrona.ErrorHandler;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.*;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import java.nio.ByteBuffer;
import java.util.EnumMap;


public class RingBufferIngress<T extends IntIdentifier> implements Agent, MessageHandler, IpcPublisher<T> {
   //Uses ReusableParameterizedMessageFactory to avoid creating temporary String objects.
    private static final Logger logger = LogManager.getLogger(RingBufferIngress.class, ReusableMessageFactory.INSTANCE);

    private RingBuffer ringBuffer;
    private final StringBuilder logAppender = new StringBuilder();
    private final EnumMap<InternalMsgType, IpcPublisher<InternalMsgType>> mapToMessagePublisher;


    public RingBufferIngress(IdleStrategy idleStrategy, int ringBufferSize, EnumMap<InternalMsgType, IpcPublisher<InternalMsgType>> mapToMessagePublisher ) {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ringBufferSize + RingBufferDescriptor.TRAILER_LENGTH);
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(byteBuffer);
        this.mapToMessagePublisher = mapToMessagePublisher;
        //todo inject ring buffer object (factory)
        this.ringBuffer = new OneToOneRingBuffer(unsafeBuffer);
        // Initialize the time wheel (resolution: 1ms, ticks per wheel: 1024)
        final ErrorHandler errorHandler = Throwable::printStackTrace;
        final AgentRunner runner = new AgentRunner(
                idleStrategy,
                errorHandler,
                null,      // You can provide a human-readable name or "null"
                this
        );
        // 5) Launch the runner on a new thread, which calls doWork() in a loop
        AgentRunner.startOnThread(runner);
    }

    @Override
    public int doWork() throws Exception {
        int workDone = 0;
        // Process pending messages
        workDone += ringBuffer.read(this);
        return workDone;
    }

    @Override
    public String roleName() {
        return "T_BLP";
    }

    @Override
    public void publish(T msgType, DirectBuffer directBuffer, int offset, int length) {
        if (!ringBuffer.write(msgType.getId(), directBuffer, offset, length)) {
            handleBackpressure(msgType.getId());
        }
    }

    @Override
    public void onMessage(int messageType, MutableDirectBuffer mutableDirectBuffer, int offset, int length) {
        final InternalMsgType internalMsgType = InternalMsgType.fromId(messageType);
        final IpcPublisher<InternalMsgType> publisher = mapToMessagePublisher.get(internalMsgType);

        if(publisher != null) {
            publisher.publish(internalMsgType, mutableDirectBuffer, offset, length);
        }
    }

    private void handleBackpressure(int msgType) {
        //do nothing
        //for backpressure, write to chronicle queue
        logger.log(Level.WARN, "Dropping msg as was full");
    }



}
