package com.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LogManager.getLogger(WebSocketInitializer.class);
    private final URI uri;
    private final NettyWebSocketClient client;
    private final  SimpleChannelInboundHandler<WebSocketFrame>  webSocketHandler;


    public WebSocketInitializer(URI uri, NettyWebSocketClient client, SimpleChannelInboundHandler<WebSocketFrame> handler) {
        this.uri = uri;
        this.client = client;
        this.webSocketHandler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.debug("Initializing channel: {}", ch);
        ChannelPipeline pipeline = ch.pipeline();
        
        // Add SSL handler if using wss://
        if ("wss".equalsIgnoreCase(uri.getScheme())) {
            logger.debug("Adding SSL handler for WSS connection");
            pipeline.addLast(new SslHandler(SslContextBuilder.forClient().build().newEngine(ch.alloc())));
        }

        logger.debug("Adding HTTP codec and aggregator to pipeline");
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(8192));

        logger.debug("Preparing WebSocket handshaker for URI: {}", uri);
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
            uri,
            WebSocketVersion.V13,
            null,  // no subprotocol
            true,  // allow extensions
            client.getHeaders()  // Use the headers from the client
        );

        logger.debug("Adding WebSocket protocol handler and Binance handler to pipeline");
        pipeline.addLast(new WebSocketClientProtocolHandler(handshaker));
        pipeline.addLast(webSocketHandler);
    }
}