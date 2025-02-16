package com.lion.aeron.shared;

import io.aeron.driver.MediaDriver;

public class StartMediaDriver {
    public static void main(String[] args) {
        MediaDriver.launch();
    }
}