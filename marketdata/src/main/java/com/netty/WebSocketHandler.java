package com.netty;

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
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);
    private final Translator translator;
    private final UnsafeBuffer messageBuffer;
    private final byte[] readBuffer;

    public WebSocketHandler(Translator translator, int bufferSize) {
        this.translator = translator;
        this.messageBuffer = new UnsafeBuffer(new byte[bufferSize]);
        this.readBuffer = new byte[bufferSize];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame frame) {
            if (frame instanceof TextWebSocketFrame textFrame) {
                ByteBuf content = textFrame.content();
                int length = content.readableBytes();
                
                // Read into our byte array
                content.readBytes(readBuffer, 0, length);
                messageBuffer.wrap(readBuffer, 0, length);

                // Translate to SBE format
                final DirectBuffer sbeBuffer = translator.translate(messageBuffer, 0, length);

                if (sbeBuffer != null) {
                    logger.debug("Translated message length: {}", translator.getEncodedLength());
                }
            } else {
                logger.warn("Unsupported frame type: {}", frame.getClass().getName());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("WebSocket error", cause);
        ctx.close();
    }
}