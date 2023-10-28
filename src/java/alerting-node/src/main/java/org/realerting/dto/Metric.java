package org.realerting.dto;

public class Metric {
    private final int id;

    private final double threshold;

    public Metric() {
        this(0, 0);
    }

    public Metric(int id, double threshold) {
        this.id = id;
        this.threshold = threshold;
    }

    public int getId() {
        return id;
    }

    public double getThreshold() {
        return threshold;
    }
}
