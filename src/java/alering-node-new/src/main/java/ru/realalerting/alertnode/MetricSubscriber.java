package ru.realalerting.alertnode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.protocol.Metric;
import ru.realalerting.protocol.RealAlertingConfig;
import ru.realalerting.protocol.RealAlertingDriverContext;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetricSubscriber implements FragmentHandler, AutoCloseable, Runnable  {

    private final MetricClient metricClient;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Consumer consumer;
    private final IdleStrategy idle;
    private int retries = 1;
    private int batchSize = 20;

    public MetricSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                            IdleStrategy idleStrategy, MetricClient metricClient) {
        this.metricClient = metricClient;
        this.idle = idleStrategy;
        consumer = new Consumer(aeronContext, connectInfo);
    }

    public MetricSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                            IdleStrategy idleStrategy, MetricClient metricClient, String configPath) {
        this.metricClient = metricClient;
        this.idle = idleStrategy;
        consumer = new Consumer(aeronContext, connectInfo);
        readConfig(configPath);
    }

    public MetricSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                            IdleStrategy idleStrategy, MetricClient metricClient, int retries, int batchSize) {
        this.metricClient = metricClient;
        this.idle = idleStrategy;
        consumer = new Consumer(aeronContext, connectInfo);
        this.retries = retries;
        this.batchSize = batchSize;
    }

    private boolean readConfig(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode metricSubscriberYamlSection = objectMapper.readTree(fileYamlConfiguration).get("info");
            retries = metricSubscriberYamlSection.get("retries").asInt();
            batchSize = metricSubscriberYamlSection.get("batch-size").asInt();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean getIsRunning() {
        return isRunning.get();
    }

    public void start() throws Exception {
        isRunning.set(true);
        if (!consumer.waitUntilConnected(retries)) {
            throw new Exception("Cannot connect to channel");
        }
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        for (int i = 0; i * Metric.BYTES < length; ++i){
            int id = directBuffer.getInt(offset + i * Metric.BYTES + Metric.OFFSET_ID);
            long value = directBuffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_VALUE);
            long timestamp = directBuffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_TIMESTAMP);
            metricClient.calculateAlert(id, value, timestamp);
        }
    }

    @Override
    public void close() {
        if (isRunning.get()) {
            consumer.close();
        }
    }

    @Override
    public void run() {
        while (isRunning.get()) {
            int poll = consumer.getSubscription().poll(this, batchSize * Metric.BYTES);
            if (poll < 0) {
                idle.idle(poll);
            }
        }
        close();
    }
}
