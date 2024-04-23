package ru.realalerting.alert_node;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.protocol.AeronContext;
import ru.realalerting.protocol.ConnectInfo;
import ru.realalerting.protocol.Metric;

import java.util.concurrent.atomic.AtomicBoolean;

public class MetricSubscriber implements FragmentHandler, AutoCloseable, Runnable  {

    private final MetricClient metricClient;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Consumer consumer;
    private final IdleStrategy idle;

    public MetricSubscriber(AeronContext aeronContext, ConnectInfo connectInfo,
                            IdleStrategy idleStrategy, MetricClient metricClient) {
        this.metricClient = metricClient;
        this.idle = idleStrategy;
        consumer = new Consumer(aeronContext, connectInfo);
    }

    public boolean getIsRunning() {
        return isRunning.get();
    }

    public void start() throws Exception {
        start(3);
    }

    public void start(int retries) throws Exception {
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
            int poll = consumer.getSubscription().poll(this, 20 * Metric.BYTES);
            if (poll < 0) {
                idle.idle(poll);
            }
        }
        close();
    }
}
