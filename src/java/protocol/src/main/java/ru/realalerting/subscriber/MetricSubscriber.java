package ru.realalerting.subscriber;

import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
public abstract class MetricSubscriber extends BaseSubscriber {

    public MetricSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                            IdleStrategy idleStrategy) {
        super(new Subscriber(aeronContext, connectInfo, idleStrategy));
    }

    public MetricSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(new Subscriber(aeronContext, connectInfo));
    }

    public MetricSubscriber(Subscriber subscriber) {
        super(subscriber);
    }

    @Override
    public int doWork() {
        int poll = subscriber.getSubscription().poll(this,
                subscriber.getMaxFetchBytes() * MetricConstants.METRIC_BYTES);
        return 0;
    }

    @Override
    public String roleName() {
        return "metric receiver";
    }
}