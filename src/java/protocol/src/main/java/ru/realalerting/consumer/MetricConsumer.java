package ru.realalerting.consumer;

import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
public abstract class MetricConsumer extends BaseConsumer  {

    public MetricConsumer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                          IdleStrategy idleStrategy) {
        super(new Consumer(aeronContext, connectInfo, idleStrategy));
    }

    public MetricConsumer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(new Consumer(aeronContext, connectInfo));
    }

    @Override
    public void run() {
        while (isRunning.get()) {
            int poll = consumer.getSubscription().poll(this,
                    consumer.getMaxFetchBytes() * MetricConstants.BYTES);
            if (poll <= 0) {
                consumer.getIdle().idle(poll);
            }
        }
        close();
    }
}