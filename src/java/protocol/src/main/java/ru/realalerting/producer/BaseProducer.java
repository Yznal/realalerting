package ru.realalerting.producer;

import io.aeron.logbuffer.BufferClaim;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseProducer {
    protected final Producer producer;
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);
    protected final BufferClaim bufferClaim = new BufferClaim();


    public BaseProducer(Producer producer) {
        this.producer = producer;
    }

    public BaseProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        producer = new Producer(aeronContext, connectInfo);
    }

    public BaseProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        producer = new Producer(aeronContext, new RealAlertingConfig(uri, streamId));
    }

    public BaseProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        producer = new Producer(aeronContext, new RealAlertingConfig(streamId, isIpc));
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void start(int retries) throws Exception {
        isRunning.set(true);
        if (!producer.waitUntilConnected(retries)) {
            throw new Exception("Cannot connect to channel");
        }
    }

    public void start() throws Exception {
        isRunning.set(true);
        producer.waitUntilConnected();
    }

    public void waitUntilConnected() {
        producer.waitUntilConnected();
    }
}
