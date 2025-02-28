package com.app;

import com.bootstrap.ExecutionServiceBootstrap;
import com.lion.config.ConfigLoader;

public class ExecutionServiceMain {
    public static void main(String[] args) {
        final ConfigLoader configLoader = new ConfigLoader("app.properties");
        final ExecutionServiceBootstrap appBootstrap = new ExecutionServiceBootstrap(configLoader);
    }
}
