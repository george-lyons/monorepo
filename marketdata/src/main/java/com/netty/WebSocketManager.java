package com.netty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WebSocketManager {
    private static final Logger logger = LogManager.getLogger(WebSocketManager.class);

    private final List<NettyWebSocketClient> clients = new ArrayList<>();

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
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        logger.info("Shutting down WebSocket clients...");
        clients.forEach(NettyWebSocketClient::shutdown);
    }

    public static void main(String[] args) {
        new WebSocketManager().start();
    }
}