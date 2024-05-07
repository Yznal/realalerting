package ru.realalerting.metrciclient;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.agrona.DirectBuffer;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.Protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class MetricRegistry implements FragmentHandler {
    private AtomicInteger tempId = new AtomicInteger(-1); // они всегда отрицательные
    // TODO не нужен создавать временные metricId

    // TODO requestId который генерится для запросов и передаем его в ClientConsumer, для хранения контекста запроса
    // TODO для новой метрики хранит саму Metric
    // TODO Map <requestId, Object>
    private Int2ObjectOpenHashMap<Metric> metrics = new Int2ObjectOpenHashMap<>();
    private Object2IntOpenHashMap<String[]> tagsIdMetrics = new Object2IntOpenHashMap<>(3000); // TODO ConcurentHashMap
    private Int2ObjectOpenHashMap<String[]> idTagsMetrics = new Int2ObjectOpenHashMap<>(); // TODO удалить, т.к. не нужны временные id
    // TODO не париться насчет уникальности по id и по tags
    private ClientProducer producer;
    private ClientConsumer consumer;
    private ClientMetricProducer metricProducer;
    private static MetricRegistry registry = null;

    public static MetricRegistry getInstance() {
        if (registry == null) {
            throw new IllegalStateException("MetricRegistry not initialized");
        }
        return registry;
    }

    public void initialize(Producer producer, Consumer consumer, ClientMetricProducer metricProducer) {
        if (registry == null) {
            registry = new MetricRegistry(producer, consumer, metricProducer);
        }
    }

    private MetricRegistry(Producer producer, Consumer consumer, ClientMetricProducer metricProducer) {
        this.producer = new ClientProducer(producer);
        this.consumer = new ClientConsumer(consumer);
        this.metricProducer = metricProducer;
    }

    public Metric getMetric(String[] tags) {
        if (tagsIdMetrics.containsKey(tags)) {
            return metrics.get(tagsIdMetrics.get(tags));
        }
        producer.getMetricId(tags);
        Metric metric = new Metric(tempId.get(), producer, consumer, metricProducer);
        tagsIdMetrics.put(tags, tempId.get());
        idTagsMetrics.put(tempId.get(), tags);
        metrics.put(tempId.getAndAdd(-1), metric);
        return metric;
    }

    public Metric getMetric(int id) {
        return metrics.get(id);
    }

    void changeMetric(int oldId, int newId) {
        Metric metric = metrics.get(oldId);
        metrics.remove(oldId);
        metrics.put(newId, metric);
        String[] tags = idTagsMetrics.get(oldId);
        tagsIdMetrics.replace(tags, newId);
        idTagsMetrics.remove(oldId);
        idTagsMetrics.put(newId, tags);
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        int instructionId = directBuffer.getInt(offset);
        switch (instructionId) {
            case Protocol.INSTRUCTION_SET_METRIC_ID -> {
                int[] ids = consumer.setMetricId(directBuffer, offset + ru.realalerting.protocol.Metric.LENGTH_ID, length, header);
                changeMetric(ids[0], ids[1]);
            }
            default -> throw new IllegalStateException("Invalid instruction id: " + instructionId);
        }
    }
}
