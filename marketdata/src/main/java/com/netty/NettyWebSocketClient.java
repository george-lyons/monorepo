package com.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class NettyWebSocketClient {
    private static final Logger logger = LogManager.getLogger(NettyWebSocketClient.class);
    private final String name;
    private final String url;
    private final EventLoopGroup group;
    private final HttpHeaders headers;
    private Channel channel;

    public NettyWebSocketClient(String name, String url, EventLoopGroup group, HttpHeaders headers) {
        this.name = name;
        this.url = url;
        this.group = group;
        this.headers = headers;
    }

    public void connect() {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 443 : uri.getPort();

            logger.info("[{}] Connecting to WebSocket: {}", name, url);
            logger.info("[{}] Host: {}, Port: {}", name, host, port);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new WebSocketInitializer(uri, this));

            logger.info("[{}] 🔄 Initiating connection...", name);
            ChannelFuture future = bootstrap.connect(host, port);

            future.addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    logger.info("[{}] ✅ Successfully connected to WebSocket.", name);
                    channel = f.channel();
                } else {
                    logger.error("[{}] ❌ Connection failed. Retrying in 5 seconds...", name, f.cause());
                    retryConnect();
                }
            });

            future.channel().closeFuture().addListener((ChannelFutureListener) f -> {
                logger.warn("[{}] ❌ WebSocket closed. Reconnecting...", name);
                retryConnect();
            });

        } catch (Exception e) {
            logger.error("[{}] ❌ Error in connection: {}", name, e.getMessage());
            retryConnect();
        }
    }

    private void retryConnect() {
        group.schedule(this::connect, 5, TimeUnit.SECONDS);
    }

    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) {
            //reusable object no need to allocate
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}