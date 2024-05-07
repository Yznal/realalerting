package ru.realalerting.reader;

/**
 * @author Karbayev Saruar
 */
public class MetricConsumerConfig {
    private int retryCount;
    private int maxFetchBytes;

    public MetricConsumerConfig(int retryCount, int maxFetchBytes) {
        this.retryCount = retryCount;
        this.maxFetchBytes = maxFetchBytes;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxFetchBytes() {
        return maxFetchBytes;
    }
}
