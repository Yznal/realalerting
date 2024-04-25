package ru.realalerting.alertnode;

enum Comparison{
    GREATER,
    LESS,
    EQUAL,
    GREATER_OR_EQUAL,
    LESS_OR_EQUAL
}

public class AlertInfo {
    private final int alertId;
    private final int metricId;
    private final Comparison comp;
    private final long threshold;

    public AlertInfo(int alertId, int metricId, Comparison comp, long threshold) {
        this.alertId = alertId;
        this.metricId = metricId;
        this.comp = comp;
        this.threshold = threshold;
    }

    public int getAlertId() {
        return alertId;
    }

    public int getMetricId() {
        return metricId;
    }

    public Comparison getComp() {
        return comp;
    }

    public long getThreshold() {
        return threshold;
    }
}
