package org.realerting.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author shadrin.m.dm@sberbank.ru
 */
public final class MetricReceivedEvent {

    private int metricId;
    private double value;

    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public final static EventFactory<MetricReceivedEvent> EVENT_FACTORY = MetricReceivedEvent::new;

    @Override
    public String toString() {
        return "MetricReceivedEvent{metricId=" + metricId + ", value= " + value + "}";
    }
}
