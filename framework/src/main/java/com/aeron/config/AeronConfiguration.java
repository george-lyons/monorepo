package com.aeron.config;

public class AeronConfiguration  {
    // IPC channel for same-host communication
    private final String channel;
    private final int streamId;
    private final String aeronDirectoryName;
    
    // Default values for IPC
    public static final String DEFAULT_IPC_CHANNEL = "aeron:ipc";
    public static final int DEFAULT_STREAM_ID = 1001;
    public static final String DEFAULT_AERON_DIR = "/tmp/logs/aeron";

    public AeronConfiguration(String channel, int streamId, String aeronDirectoryName) {
        this.channel = channel;
        this.streamId = streamId;
        this.aeronDirectoryName = aeronDirectoryName;
    }

    // Static factory method for default IPC configuration
    public static AeronConfiguration createDefault() {
        return new AeronConfiguration(
            DEFAULT_IPC_CHANNEL,
            DEFAULT_STREAM_ID,
            DEFAULT_AERON_DIR
        );
    }

    // Getters
    public String getChannel() {
        return channel;
    }

    public int getStreamId() {
        return streamId;
    }

    public String getAeronDirectoryName() {
        return aeronDirectoryName;
    }

    // Implement Appendable interface

    public void appendTo(StringBuilder builder) {
        builder.append("AeronConfiguration{")
                .append("channel='").append(channel).append('\'')
                .append(", streamId=").append(streamId)
                .append(", aeronDirectoryName='").append(aeronDirectoryName).append('\'')
                .append('}');
    }


    // Builder for custom configurations
    public static class Builder {
        private String channel = DEFAULT_IPC_CHANNEL;
        private int streamId = DEFAULT_STREAM_ID;
        private String aeronDirectoryName = DEFAULT_AERON_DIR;

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder streamId(int streamId) {
            this.streamId = streamId;
            return this;
        }

        public Builder aeronDirectoryName(String aeronDirectoryName) {
            this.aeronDirectoryName = aeronDirectoryName;
            return this;
        }

        public AeronConfiguration build() {
            return new AeronConfiguration(channel, streamId, aeronDirectoryName);
        }
    }
} 