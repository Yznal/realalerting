package org.protocol;

public class Metric<T> {
    private final int id;
    private final int timestamp;
    private final T value;

    public Metric(int id, int timestamp, T value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public T getValue() {
        return value;
    }
}
