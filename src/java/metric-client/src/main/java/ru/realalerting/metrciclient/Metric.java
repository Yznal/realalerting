package ru.realalerting.metrciclient;

import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpmcArrayQueue;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.reader.RealAlertingConfig;

import java.util.ArrayList;
import java.util.List;

public class Metric implements AutoCloseable {
    private volatile int metricId = -1;
    private volatile int alertId = -1;
    private MetricRegistry metricRegistry;
    private AlertProducer alertProducer;
    private List<AlertLogicBase> alertLogicBases = new ArrayList<>();
    private volatile MpmcArrayQueue<MetricData> q = new MpmcArrayQueue<>(3000000);

    private static class MetricData {
        long value;
        long timestamp;

        public MetricData(long value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private final MessagePassingQueue.Consumer<MetricData> qSender = (x) -> {
        if (metricId < 0) {
            q.add(x);
        } else {
            if (alertProducer != null) {
                sendToAllAlerts(x.value, x.timestamp);
            }
            metricRegistry.getMetricProducer().sendSingleMetric(metricId, x.value, x.timestamp);
        }
    };
    
    Metric(MetricRegistry metricRegistry) {
        this.metricRegistry  = metricRegistry;
    }

    Metric(MetricRegistry metricRegistry, List<AlertLogicBase> alertLogics) {
        this.metricRegistry  = metricRegistry;
        this.alertLogicBases = alertLogics;
    }

    Metric(MetricRegistry metricRegistry, int metricId) {
        this.metricRegistry  = metricRegistry;
        this.metricId = metricId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public void setAlertProducer(Producer alertProducer) {
        this.alertProducer = new AlertProducer(alertProducer);
    }

    public void setAlertProducer(AlertProducer alertProducer) {
        this.alertProducer = alertProducer;
    }

    public void setAlertProducer(RealAlertingConfig connectInfo) {
        RealAlertingDriverContext driverContext = MetricRegistry.getInstance().getDriverContext();
        this.alertProducer = new AlertProducer(driverContext, connectInfo);
    }

    public void setAlertProducer(String path) {
        RealAlertingDriverContext driverContext = MetricRegistry.getInstance().getDriverContext();
        this.alertProducer = new AlertProducer(driverContext, ConfigReader.readProducerFromFile(path));
    }

    public void addAlertLogic(AlertLogicBase alertLogic) {
        alertLogicBases.add(alertLogic);
    }

    public int getMetricId() {
        return metricId;
    }

    private void sendToAllAlerts(long value, long timestamp) {
        for (int i = 0; i < alertLogicBases.size(); i++) {
            alertProducer.sendAlertWithAlertId(alertLogicBases.get(i),
                    alertLogicBases.get(i).getAlertInfo().getAlertId(), metricId, value, timestamp);
        }
    }

    void changeId(int newId) {
        metricId = newId;
        synchronized (q) {
            if (q != null) {
                cleanQueue();
                q = null;
            }
        }
    }

    public boolean addValue(long value, long timestamp) {
        var q = this.q;
        if (q == null) {
            if (alertProducer != null) {
                sendToAllAlerts(value, timestamp);
            }
            return metricRegistry.getMetricProducer().sendSingleMetric(metricId, value, timestamp);
        }
        synchronized (q) {
            if (this.q != null) {
                if (metricId == -1) {
                    q.add(new MetricData(value, timestamp));
                    return false;
                } else {
                    cleanQueue();
                    this.q = null;
                }
            }
        }
        if (alertProducer != null) {
            sendToAllAlerts(value, timestamp);
        }
        return metricRegistry.getMetricProducer().sendSingleMetric(metricId, value, timestamp);
    }

    private void cleanQueue() {
        if (!q.isEmpty()) {
            q.drain(qSender);
        }
    }

    @Override
    public void close() {
        if (alertProducer != null) {
            alertProducer.close(); // TODO вызывать close при удалении объекта, но finalize deprecated
        }
    }
}
