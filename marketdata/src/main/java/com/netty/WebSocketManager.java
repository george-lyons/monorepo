package com.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WebSocketManager {
    private static final Logger logger = LogManager.getLogger(WebSocketManager.class);
    private static final String AERON_CHANNEL = "aeron:ipc";
    private static final int STREAM_ID = 1001;
    private final NioEventLoopGroup group;

    private final List<NettyWebSocketClient> clients = new ArrayList<>();

    public WebSocketManager() {
        this.group = new NioEventLoopGroup(1); // Single-threaded event loop
    }

    public void addClient(NettyWebSocketClient nettyWebSocketClient) {
        clients.add(nettyWebSocketClient);
    }

    public void start() {
        logger.info("🚀 Starting WebSocket clients...");

        clients.forEach(client -> {
            logger.info("🚀 Starting WebSocket clients: {}", client.getUrl());
            client.connect();
        });

        // Wait indefinitely
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down WebSocket clients...");
            clients.forEach(NettyWebSocketClient::shutdown);
        }));
    }

    public static void main(String[] args) {
        new WebSocketManager().start();
    }
}