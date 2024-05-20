package ru.realalerting.subscriber;

import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseSubscriber implements FragmentHandler, AutoCloseable, Agent {
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);
    protected final Subscriber subscriber;
    private int maxFragments = 1000;

    public BaseSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public boolean getIsRunning() {
        return isRunning.get();
    }

    public Subscriber getConsumer() {
        return subscriber;
    }

    public void start() {
        final AgentRunner receiveAgentRunner = new AgentRunner(subscriber.getIdle(), Throwable::printStackTrace, null, this);
        AgentRunner.startOnThread(receiveAgentRunner);
    }

    @Override
    public int doWork() {
        subscriber.getSubscription().poll(this, maxFragments);
        return 0;
    }

    @Override
    public void close() {
        subscriber.close();
    }

    @Override
    public String roleName() {
        return "receiver";
    }
}
