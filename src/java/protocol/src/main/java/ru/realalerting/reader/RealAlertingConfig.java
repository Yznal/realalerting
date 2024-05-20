package ru.realalerting.reader;

/**
 * @author Karbayev Saruar
 */
public class RealAlertingConfig {
    private String uri;
    private int streamId;

    public RealAlertingConfig(String uri, int streamId) {
        this.uri = uri;
        this.streamId = streamId;
    }

    public RealAlertingConfig(String uri, int streamId, boolean isIpcEnabled) {
        this.uri = uri;
        this.streamId = streamId;
    }

    public RealAlertingConfig(int streamId, boolean isIpcEnabled) {
        this.streamId = streamId;
    }

    public String getUri() {
        return uri;
    }

    public int getStreamId() {
        return streamId;
    }
}
