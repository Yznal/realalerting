package ru.realalerting.consumer;

import io.aeron.Aeron;
import io.aeron.Subscription;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import ru.realalerting.reader.MetricConsumerConfig;
import ru.realalerting.reader.RealAlertingConfig;
import ru.realalerting.protocol.RealAlertingDriverContext;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;


/**
 * @author Karbayev Saruar
 */
public class Consumer implements AutoCloseable {
    private final Subscription subscription;
    private final IdleStrategy idle;
    private int retryCount = 1;
    private int maxFetchBytes = 20;

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idle,
                    int retryCount, int maxFetchBytes) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        this.idle = idle;
        this.retryCount = retryCount;
        this.maxFetchBytes = maxFetchBytes;
    }

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig,
                    int retryCount, int maxFetchBytes) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
        this.retryCount = retryCount;
        this.maxFetchBytes = maxFetchBytes;
    }

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idle,
                    MetricConsumerConfig config) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        this.idle = idle;
        this.retryCount = config.getRetryCount();
        this.maxFetchBytes = config.getMaxFetchBytes();
    }

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig,
                    MetricConsumerConfig config) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
        this.retryCount = config.getRetryCount();
        this.maxFetchBytes = config.getMaxFetchBytes();
    }

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
    }

    public Consumer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idleStrategy) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = idleStrategy;
    }

    public IdleStrategy getIdle() {
        return idle;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public int getMaxFetchBytes() {
        return maxFetchBytes;
    }

    public boolean isConnected() {
        return subscription.isConnected();
    }

    public boolean waitUntilConnected() {
        AtomicInteger count = new AtomicInteger(0);
        while(count.get() < retryCount && !subscription.isConnected()) {
            idle.idle();
            count.incrementAndGet();
        }
        return count.get() < retryCount;
    }

    public void aeronReconnect(RealAlertingDriverContext aeronContext) {
        // TODO вечный aeron reconnect
        try {
            Aeron aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(
                    aeronContext.getMediaDriver().aeronDirectoryName()));
//            subscription = aeron.addSubscription()
        } catch (Exception e) {
            aeronReconnect(aeronContext);
        }
    }

    @Override
    public void close() {
        if (!isNull(subscription)) {
            subscription.close();
        }
    }
}