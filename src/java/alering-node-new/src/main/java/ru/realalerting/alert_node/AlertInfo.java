package ru.realalerting.alert_node;

enum Comparison{
    GREATER,
    LESS,
    EQUAL,
    GREATER_OR_EQUAL,
    LESS_OR_EQUAL
}

public class AlertInfo {
    private final int metricId;
    private final Comparison comp;
    private final long threshold;

    public AlertInfo(int metricId, Comparison comp, long threshold) {
        this.metricId = metricId;
        this.comp = comp;
        this.threshold = threshold;
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
