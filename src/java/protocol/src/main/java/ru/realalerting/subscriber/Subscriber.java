package ru.realalerting.subscriber;

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
public class Subscriber implements AutoCloseable {
    private Subscription subscription;
    private final IdleStrategy idle;
    private int retryCount = 1;
    private int maxFetchBytes = 20;

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idle,
                      int retryCount, int maxFetchBytes) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        this.idle = idle;
        this.retryCount = retryCount;
        this.maxFetchBytes = maxFetchBytes;
    }

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig,
                      int retryCount, int maxFetchBytes) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
        this.retryCount = retryCount;
        this.maxFetchBytes = maxFetchBytes;
    }

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idle,
                      MetricConsumerConfig config) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        this.idle = idle;
        this.retryCount = config.getRetryCount();
        this.maxFetchBytes = config.getMaxFetchBytes();
    }

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig,
                      MetricConsumerConfig config) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
        this.retryCount = config.getRetryCount();
        this.maxFetchBytes = config.getMaxFetchBytes();
    }

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig) {
        subscription = aeronContext.getAeron().addSubscription(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
    }

    public Subscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idleStrategy) {
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

    public void aeronReconnect(RealAlertingDriverContext aeronContext, RealAlertingConfig config) {
        try {
            Aeron aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(
                    aeronContext.getMediaDriver().aeronDirectoryName()));
            subscription = aeron.addSubscription(config.getUri(), config.getStreamId());
        } catch (Exception e) {
            aeronReconnect(aeronContext, config);
        }
    }

    @Override
    public void close() {
        if (!isNull(subscription)) {
            subscription.close();
        }
    }
}