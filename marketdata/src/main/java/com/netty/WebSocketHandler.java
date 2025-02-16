package com.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);
    private final NettyWebSocketClient client;

    public WebSocketHandler(NettyWebSocketClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame frame) {
            if (frame instanceof TextWebSocketFrame textFrame) {
                String message = textFrame.text();
                logger.info("Received message: {}", message);
                //publish to aeron IPC with message type ETC, maybe we want a header for message
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