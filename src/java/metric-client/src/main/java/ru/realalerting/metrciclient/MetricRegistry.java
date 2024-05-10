package ru.realalerting.metrciclient;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.agrona.DirectBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricRegistry implements FragmentHandler {
    private AtomicInteger requsetId = new AtomicInteger(0); // они всегда отрицательные
    private Int2ObjectOpenHashMap<Object> requestContexts = new Int2ObjectOpenHashMap<>();
    private Int2ObjectOpenHashMap<Metric> metricByIds = new Int2ObjectOpenHashMap<>();
    private ConcurrentHashMap<String[], Metric> metricByTags = new ConcurrentHashMap<>();
    private ClientProducer producer;
    private MetricProcessor consumer;
    private ClientMetricProducer metricProducer;
    private static MetricRegistry registry = null;


    public static MetricRegistry getInstance() {
        if (registry == null) {
            throw new IllegalStateException("MetricRegistry not initialized");
        }
        return registry;
    }

    public static void initialize(Producer producer, ClientMetricProducer metricProducer) {
        if (registry == null) {
            registry = new MetricRegistry(producer, metricProducer);
        }
    }

    private MetricRegistry(Producer producer, ClientMetricProducer metricProducer) {
        this.producer = new ClientProducer(producer);
        this.consumer = new MetricProcessor();
        this.metricProducer = metricProducer;
    }

    public ClientProducer getProducer() {
        return producer;
    }

    public MetricProcessor getConsumer() {
        return consumer;
    }

    public ClientMetricProducer getMetricProducer() {
        return metricProducer;
    }

    public Metric getMetric(String[] tags) { // паттерн для правильной записи в ConcurrentMap
        Metric gettedMetric = metricByTags.get(tags);
        if (gettedMetric != null) {
            return gettedMetric;
        }
        gettedMetric = new Metric(getInstance(), metricProducer);
        Metric metric = metricByTags.putIfAbsent(tags, gettedMetric);
        if (metric != null) {
            gettedMetric = metric;
        } else {
            int curRequestId = requsetId.getAndIncrement();
            requestContexts.put(curRequestId, tags);
            producer.getMetricId(curRequestId, tags); // TODO what if not sent
        }
        return gettedMetric;
    }

    public Metric getMetric(int metricId) { // TODO выше паттерн заюзать
        if (metricByIds.containsKey(metricId)) {
            return metricByIds.get(metricId);
        }
        Metric metric = new Metric(getInstance(), metricProducer);
        metricByIds.put(metricId, metric); // TODO Actor для записи в Map
        return metric;
    }

    void changeMetric(int requestId, int newId) {
        if (requestContexts.containsKey(requestId)) { // TODO выше паттерн заюзать
            String[] tags = (String[]) requestContexts.get(requestId);
            metricByTags.get(tags).changeId(newId);
        } else {
            throw new IllegalArgumentException("requestId=" + requestId + " not exists");
        }
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        int instructionId = directBuffer.getInt(offset);
        switch (instructionId) {
            case Protocol.INSTRUCTION_SET_METRIC_ID -> {
                int[] ids = consumer.setMetricId(directBuffer, offset + MetricConstants.LENGTH_ID, length, header);
                changeMetric(ids[0], ids[1]);
            }
            default -> throw new IllegalStateException("Invalid instruction id: " + instructionId);
        }
    }
}
