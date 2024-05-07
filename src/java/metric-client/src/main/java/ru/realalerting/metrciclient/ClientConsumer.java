package ru.realalerting.metrciclient;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.protocol.Metric;
import ru.realalerting.protocol.Protocol;

public class ClientConsumer { // TODO MetricProcessor переименовать
    private Consumer consumer;

    public ClientConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public int[] setMetricId(DirectBuffer directBuffer, int offset, int length, Header header) {
        int oldMetricId = directBuffer.getInt(offset);
        int newMetricId = directBuffer.getInt(offset + Metric.LENGTH_ID);
        // TODO мы передаем еще контекст
        return new int[]{oldMetricId, newMetricId}; // TODO отправить в MetricRegistry
    }
}
