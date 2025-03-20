package com.app;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MediaDriverLauncher {
    private static final Logger logger = LoggerFactory.getLogger(MediaDriverLauncher.class);


    public static void main(String[] args) {
        logger.info("Starting standalone Aeron Media Driver...");

        // Check if the directory is already in use
        File aeronDir = new File("/tmp/aeron/logs");
        if (aeronDir.exists() && aeronDir.list().length > 0) {
            logger.warn("Aeron directory is already in use: {}", aeronDir.getAbsolutePath());
        }

        //TODO - Configure Media Driver for IPC - we need to inject this and have the configurations in a file
        final MediaDriver.Context ctx = new MediaDriver.Context()
                .threadingMode(ThreadingMode.SHARED)
                .dirDeleteOnStart(false) // Avoids deleting an in-use directory
                .dirDeleteOnShutdown(false)
                .aeronDirectoryName("/tmp/aeron/logs");

        try (MediaDriver driver = MediaDriver.launch(ctx)) {
            logger.info("Media Driver started, directory: {}", ctx.aeronDirectoryName());

            // Keep the driver running until shutdown signal
            new ShutdownSignalBarrier().await();

            logger.info("Shutdown signal received. Stopping Media Driver...");
        }

        logger.info("Media Driver stopped");
    }
}