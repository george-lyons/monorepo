package com.binance;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;

public class BinanceWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final BinanceNettyWebSocketClient client;

    public BinanceWebSocketHandler(BinanceNettyWebSocketClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String message = ((TextWebSocketFrame) frame).text();
            System.out.println("Market Data: " + message);
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket closed. Reconnecting...");
            client.connect();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        client.connect();  // Reconnect on failure
    }
}