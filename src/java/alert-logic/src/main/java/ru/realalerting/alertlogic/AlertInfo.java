package ru.realalerting.alertlogic;

public class AlertInfo { // TODO record
    // TODO перенести всю инфу в саму реализацию
    private final int alertId;
    private final int metricId;
    private final long threshold;

    public AlertInfo(int alertId, int metricId, long threshold) {
        this.alertId = alertId;
        this.metricId = metricId;
        this.threshold = threshold;
    }

    public int getAlertId() {
        return alertId;
    }

    public int getMetricId() {
        return metricId;
    }

    public long getThreshold() {
        return threshold;
    }
}
