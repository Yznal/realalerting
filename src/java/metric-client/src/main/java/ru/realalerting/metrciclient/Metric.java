package ru.realalerting.metrciclient;

import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpmcArrayQueue;

import java.time.Instant;
import java.util.function.Consumer;

public class Metric {
    private int metricId = -1;
    private String[] tags;
    private ClientMetricProducer metricProducer;
    private final ClientProducer producer;
    private final ClientConsumer consumer; // TODO consumer и producer удалить, и хранить MetricRegistry

    private static class MetricData {
        long value;
        long timestamp;

        public MetricData(long value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private MpmcArrayQueue<MetricData> q = new MpmcArrayQueue<>(3000);

    private final MessagePassingQueue.Consumer<MetricData> qSender = (x) -> {
        if (metricId < 0) {
            q.add(x);
        } else {
            metricProducer.sendMetric(metricId, x.value, x.timestamp);
        }
    };
    // TODO constructor
    Metric(int metricId, ClientProducer producer, ClientConsumer consumer, ClientMetricProducer metricProducer) {
        this.metricId = metricId;
        this.metricProducer = metricProducer; // TODO тут не знаем еще подписчиков на метрику
        this.producer = producer;
        this.consumer = consumer;
    }

    public int getMetricId() {
        return metricId;
    }

    void changeId(int newId) {
        MetricRegistry.getInstance().changeMetric(metricId, newId);
        metricId = newId;
    }

    public boolean addValue(long value, long timestamp) {
        if (metricId < 0) {
            q.add(new MetricData(value, Instant.now().getEpochSecond())); // TODO Сомнительно, создаю много, есть алтернативы?
            producer.getMetricId(tags);
            // TODO обрабатывать запрос получения metricId
            return false;
        }
        // TODO отправить значения метрики и timestamp

        // TODO если очередь не пуста и мы уже знаем metricId
        // TODO synchronized block чтобы не перемешать
        return true;
    }

    private void cleanQueue() {
        q.drain(qSender);
    }
}
