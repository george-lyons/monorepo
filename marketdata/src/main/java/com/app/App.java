package com.app;

import com.bootstrap.AppBootstrap;
import com.lion.config.ConfigLoader;

public class App {
    public static void main(String[] args) {
        final ConfigLoader configLoader = new ConfigLoader("app.properties");
        final AppBootstrap appBootstrap = new AppBootstrap(configLoader);
    }
}
