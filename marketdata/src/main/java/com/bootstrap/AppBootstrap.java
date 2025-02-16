package com.bootstrap;

import com.lion.config.ConfigLoader;
import com.service.PrintService;

public class AppBootstrap {
    public AppBootstrap(ConfigLoader configLoader) {
        final String message = configLoader.getProperty("app.message");
        final PrintService printService = new PrintService(message);
        printService.print();
    }
}
