package ru.realalerting.consumer;

import io.aeron.logbuffer.FragmentHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseConsumer implements FragmentHandler, AutoCloseable, Runnable {
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);
    protected final Consumer consumer;

    public BaseConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public boolean getIsRunning() {
        return isRunning.get();
    }

    @Override
    public void close() {
        consumer.close();
    }
}
