package com.binance;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class BinanceNettyWebSocketClient {
    private static final Logger logger = LogManager.getLogger(BinanceNettyWebSocketClient.class);
    private final String url;
    private final EventLoopGroup group;
    private Channel channel;

    public BinanceNettyWebSocketClient(String url) {      
        this.url = url;
        this.group = new NioEventLoopGroup(1);  // Single-threaded event loop for low latency
    }
    
    public void connect() {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 443 : uri.getPort();

            logger.info("Connecting to Binance WebSocket: {}", url);
            logger.info("Host: {}, Port: {}", host, port);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new BinanceWebSocketInitializer(uri, this));

            logger.info("🔄 Initiating connection...");
            ChannelFuture future = bootstrap.connect(host, port);
            
            future.addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    logger.info("✅ Successfully connected to Binance WebSocket.");
                    channel = f.channel();
                } else {
                    logger.error("❌ Connection failed. Details:");
                    logger.error("Error type: {}", f.cause().getClass().getName());
                    logger.error("Error message: {}", f.cause().getMessage());
                    logger.error("Connection failure", f.cause());
                    logger.info("Retrying in 5 seconds...");
                    retryConnect();
                }
            });

            future.addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    logger.error("❌ Connection failed. HTTP Response:");
                    if (f.cause() instanceof WebSocketClientHandshakeException) {
                        WebSocketClientHandshakeException handshakeException = (WebSocketClientHandshakeException) f.cause();
                        logger.error("🔴 Binance HTTP Response: {}", handshakeException.getMessage());
                    }
                    logger.error("Connection failure details", f.cause());
                }
            });

            future.channel().closeFuture().addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    logger.warn("❌ WebSocket closed. Reconnecting...");
                    retryConnect();
                } else {
                    logger.error("❌ WebSocket closed unexpectedly. Reason: {}", f.cause());
                }
            });

        } catch (Exception e) {
            logger.error("❌ Error in connection: {}", e.getMessage());
            retryConnect();
        }
    }

    private void retryConnect() {
        group.schedule(this::connect, 5, TimeUnit.SECONDS);
    }

    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        // FIXED: Removed trailing slash
        String binanceUrl = "wss://stream.binance.com:9443/ws/btcusdt@depth";
        BinanceNettyWebSocketClient client = new BinanceNettyWebSocketClient(binanceUrl);
        client.connect();
    }
}