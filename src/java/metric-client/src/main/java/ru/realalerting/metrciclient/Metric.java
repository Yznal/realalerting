package ru.realalerting.metrciclient;

import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpmcArrayQueue;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;

import java.time.Instant;

public class Metric {
    private int metricId = -1;
    private String[] tags;
    private ClientMetricProducer metricProducer;
    private final MetricRegistry metricRegistry;
    private AlertLogicBase alertLogic;

    private static class MetricData {
        long value;
        long timestamp;

        public MetricData(long value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private volatile MpmcArrayQueue<MetricData> q = new MpmcArrayQueue<>(3000);

    private final MessagePassingQueue.Consumer<MetricData> qSender = (x) -> {
        if (metricId < 0) {
            q.add(x);
        } else {
            metricProducer.sendMetric(metricId, x.value, x.timestamp);
        }
    };
    // TODO constructor
    Metric(MetricRegistry metricRegistry, ClientMetricProducer metricProducer) {
        this.metricRegistry  = metricRegistry;
        this.metricProducer = metricProducer; // TODO у большинства Metric не будет mdc публикаций, они будут у клиента
    }

    Metric(MetricRegistry metricRegistry, ClientMetricProducer metricProducer, AlertLogicBase alertLogic) {
        this.metricRegistry  = metricRegistry;
        this.metricProducer = metricProducer;
        this.alertLogic = alertLogic;
    }

    public int getMetricId() {
        return metricId;
    }

    void changeId(int newId) {
        metricId = newId;
    }

    public boolean addValue(long value, long timestamp) {
        if (q == null) {
            return metricProducer.sendMetric(metricId, value, timestamp);
        }
        if (metricId == -1) {
            q.add(new MetricData(value, timestamp));
            return false;
        }
        synchronized (q) {
            if (metricId != -1 && q != null) {
                cleanQueue();
                q = null;
            }
        }
        return metricProducer.sendMetric(metricId, value, timestamp);
    }

    private void cleanQueue() {
        if (!q.isEmpty()) {
            q.drain(qSender);
        }
    }
}
