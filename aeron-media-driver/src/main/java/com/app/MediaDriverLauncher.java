package com.app;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaDriverLauncher {
    private static final Logger logger = LoggerFactory.getLogger(MediaDriverLauncher.class);


    public static void main(String[] args) {
        logger.info("Starting standalone Aeron Media Driver...");

        //TODO - Configure Media Driver for IPC - we need to inject this and have the configurations in a file
        final MediaDriver.Context ctx = new MediaDriver.Context()
                .threadingMode(ThreadingMode.SHARED)
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true)
                .aeronDirectoryName("/tmp/logs/aeron");

        try (MediaDriver driver = MediaDriver.launch(ctx)) {
            logger.info("Media Driver started, directory: {}", ctx.aeronDirectoryName());

            // Keep the driver running until shutdown signal
            new ShutdownSignalBarrier().await();

            logger.info("Shutdown signal received. Stopping Media Driver...");
        }

        logger.info("Media Driver stopped");
    }
}