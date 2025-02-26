package com.app;

import com.market.data.sbe.MessageHeaderDecoder;
import com.market.data.sbe.QuoteMessageDecoder;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.SigInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;


//build an ingress for this class, and push onto a ringbuffer
public class ExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionService.class);
    private static final QuoteMessageDecoder quoteMessageDecoder = new QuoteMessageDecoder();
    private static MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    public static void main(String[] args) {
        String channel = "aeron:ipc";
        int streamId = 1001;

        final AtomicBoolean running = new AtomicBoolean(true);
        SigInt.register(() -> running.set(false));

        final Aeron.Context ctx = new Aeron.Context()
                .aeronDirectoryName("/tmp/logs/aeron");

        try (Aeron aeron = Aeron.connect(ctx);
             Subscription subscription = aeron.addSubscription(channel, streamId)) {
            
            logger.info("Started Execution Service, listening on channel: {}, streamId: {}", 
                       channel, streamId);

            FragmentHandler handler = (buffer, offset, length, header) -> {
                //need to plug into our decoder actuallu
                quoteMessageDecoder.wrapAndApplyHeader(buffer, offset, messageHeaderDecoder);
                logger.debug(quoteMessageDecoder.toString());


                // Process the market data here
            };

            while (running.get()) {
                subscription.poll(handler, 10);
                Thread.yield();
            }
        }
    }
} 