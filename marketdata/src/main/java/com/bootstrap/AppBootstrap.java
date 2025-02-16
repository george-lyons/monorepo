package com.bootstrap;

import com.lion.config.ConfigLoader;
import com.netty.WebSocketManager;

public class AppBootstrap {
    public AppBootstrap(ConfigLoader configLoader) {
        WebSocketManager webSocketManager = new WebSocketManager();
        webSocketManager.start();;
    }
}
