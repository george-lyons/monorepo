package com.binance;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class BinanceWebSocketInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LogManager.getLogger(BinanceWebSocketInitializer.class);
    private final URI uri;
    private final BinanceNettyWebSocketClient client;
    private static final String ORIGIN = "https://stream.binance.com";

    public BinanceWebSocketInitializer(URI uri, BinanceNettyWebSocketClient client) {
        this.uri = uri;
        this.client = client;
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
            new DefaultHttpHeaders()
                .add(HttpHeaderNames.HOST, uri.getHost())
                .add(HttpHeaderNames.ORIGIN, ORIGIN)
                .add(HttpHeaderNames.PRAGMA, "no-cache")
                .add(HttpHeaderNames.CACHE_CONTROL, "no-cache")
                .add(HttpHeaderNames.USER_AGENT, "Mozilla/5.0")
        );

        logger.debug("Adding WebSocket protocol handler and Binance handler to pipeline");
        pipeline.addLast(new WebSocketClientProtocolHandler(handshaker));
        pipeline.addLast(new BinanceWebSocketHandler(client));
    }
}