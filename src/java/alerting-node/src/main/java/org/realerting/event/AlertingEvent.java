package org.realerting.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author shadrin.m.dm@sberbank.ru
 */
public final class AlertingEvent {

    private int metricId;

    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public final static EventFactory<AlertingEvent> EVENT_FACTORY = AlertingEvent::new;

    @Override
    public String toString() {
        return "AlertingEvent{metricId=" + metricId + "}";
    }
}
