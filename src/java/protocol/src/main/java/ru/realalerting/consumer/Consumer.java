package ru.realalerting.consumer;

import io.aeron.Subscription;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import ru.realalerting.protocol.AeronContext;
import ru.realalerting.protocol.ConnectInfo;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;


/**
 * @author Karbayev Saruar
 */
public class Consumer implements AutoCloseable {

    private final Subscription subscription;
    private final IdleStrategy idle;

    public Consumer(AeronContext aeronContext, ConnectInfo connectInfo) {
        subscription = aeronContext.getAeron().addSubscription(connectInfo.getUri(), connectInfo.getStreamId());
        idle = new SleepingIdleStrategy();
    }

    public Consumer(AeronContext aeronContext, ConnectInfo connectInfo, IdleStrategy idleStrategy) {
        subscription = aeronContext.getAeron().addSubscription(connectInfo.getUri(), connectInfo.getStreamId());
        idle = idleStrategy;
    }

    public IdleStrategy getIdle() {
        return idle;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public boolean isConnected() {
        return subscription.isConnected();
    }

    public void waitUntilConnected() {
        while (!subscription.isConnected()) {
            idle.idle();
        }
    }

    public boolean waitUntilConnected(int retries) {
        AtomicInteger count = new AtomicInteger(0);
        while(count.get() < retries && !subscription.isConnected()) {
            idle.idle();
            count.incrementAndGet();
        }
        return count.get() < retries;
    }

    @Override
    public void close() {
        if (!isNull(subscription)) {
            subscription.close();
        }
    }
}