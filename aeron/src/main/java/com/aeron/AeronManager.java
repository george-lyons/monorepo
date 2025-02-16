package com.aeron;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.driver.MediaDriver;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

public class AeronManager {
    private static final String CHANNEL = "aeron:ipc";  // Use IPC for local low-latency messaging
    private static final int STREAM_ID = 1001;
    private final Aeron aeron;
    private final Publication publication;

    public AeronManager(IdleStrategy aeronIdleStrategy) {
        //INJECT and configurable properties
        MediaDriver driver = MediaDriver.launchEmbedded();
        Aeron.Context context = new Aeron.Context()
                .aeronDirectoryName(driver.aeronDirectoryName())
                .idleStrategy(new BusySpinIdleStrategy()); // Low-latency strategy

        this.aeron = Aeron.connect(context);
        this.publication = aeron.addPublication(CHANNEL, STREAM_ID);
    }

    public Publication getPublication() {
        return publication;
    }

    public void close() {
        publication.close();
        aeron.close();
    }
}