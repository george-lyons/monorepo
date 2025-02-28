package com.netty.handler;

import com.lion.clock.Clock;
import com.lion.message.ByteCharSequence;
import com.lion.message.FrameworkMsg;
import com.lion.message.codecs.HeaderEncoder;
import com.lion.message.publisher.IpcPublisher;
import com.market.data.Translator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * todo make generic
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);
    private final Translator translator;
    private final UnsafeBuffer messageBuffer;
    private final byte[] readBuffer;
    private final Clock clock;
    private final IpcPublisher<FrameworkMsg> publisher;
    private final ByteCharSequence jsonMsg = new ByteCharSequence();

    private final HeaderEncoder headerEncoder = new HeaderEncoder();

    public WebSocketHandler(Clock clock, Translator translator, int bufferSize, IpcPublisher<FrameworkMsg> ipcPublisher) {
        this.clock = clock;
        this.publisher = ipcPublisher;
        this.translator = translator;
        this.messageBuffer = new UnsafeBuffer(new byte[bufferSize]);
        this.readBuffer = new byte[bufferSize];
        headerEncoder.wrap(messageBuffer, 0);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        long receivedNanoTime = clock.getTimeNanos();
        if (msg instanceof TextWebSocketFrame textFrame) {
            final ByteBuf content = textFrame.content();
            int length = content.readableBytes();

            // Read into our byte array
            content.readBytes(readBuffer, 0, length);
            messageBuffer.wrap(readBuffer, 0, length);


            if (logger.isDebugEnabled()) {
                // Convert message to string (for logging)
                jsonMsg.from(readBuffer, 0, length);
                logger.info(jsonMsg);
            }

            // Translate to SBE format
            final DirectBuffer sbeBuffer = translator.translate(messageBuffer, 0, length, receivedNanoTime);

            publisher.publish(FrameworkMsg.TOB_MARKET_DATA, sbeBuffer, 0, translator.getEncodedLength());

            if (sbeBuffer != null) {
                logger.debug("Translated message length: {}", translator.getEncodedLength());
            }
        } else {
            logger.warn("Unsupported frame type: {}", msg.getClass().getName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("WebSocket error", cause);
        ctx.close();
    }
}