package ru.realalerting.protocol;

/**
 * @author Karbayev Saruar
 */
public class ConnectInfo {
    private String uri;
    private int streamId;
    private boolean isIpcEnabled = false;

    public ConnectInfo(String uri, int streamId) {
        this.uri = uri;
        this.streamId = streamId;
        this.isIpcEnabled = false;
    }

    public ConnectInfo(String uri, int streamId, boolean isIpcEnabled) {
        this.uri = uri;
        this.streamId = streamId;
        this.isIpcEnabled = isIpcEnabled;
    }

    public ConnectInfo(int streamId, boolean isIpcEnabled) {
        this.streamId = streamId;
        this.isIpcEnabled = isIpcEnabled;
    }

    public String getUri() {
        return uri;
    }

    public int getStreamId() {
        return streamId;
    }

    public boolean isIpcEnabled() {
        return isIpcEnabled;
    }
}
