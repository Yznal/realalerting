package ru.realalerting.producer;

import io.aeron.Publication;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import ru.realalerting.reader.RealAlertingConfig;
import ru.realalerting.protocol.RealAlertingDriverContext;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;


/**
 * @author Karbayev Saruar
 */
public class Producer implements AutoCloseable {
    protected Publication publication;
    protected IdleStrategy idle;

    public Producer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig) {
        this.publication = aeronContext.getAeron().addPublication(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = new SleepingIdleStrategy();
    }

    public Producer(RealAlertingDriverContext aeronContext, RealAlertingConfig realAlertingConfig, IdleStrategy idleStrategy) {
        this.publication = aeronContext.getAeron().addPublication(realAlertingConfig.getUri(), realAlertingConfig.getStreamId());
        idle = idleStrategy;
    }

    public Producer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        this.publication = aeronContext.getAeron().addPublication(uri, streamId);
        idle = new SleepingIdleStrategy();
    }

    public Producer(RealAlertingDriverContext aeronContext, String uri, int streamId, IdleStrategy idleStrategy) {
        this.publication = aeronContext.getAeron().addPublication(uri, streamId);
        idle = idleStrategy;
    }

    protected Producer() {
        publication = null;
        idle = null;
    }

    public Publication getPublication() {
        return publication;
    }

    public boolean isConnected() {
        return publication.isConnected();
    }

    public IdleStrategy getIdle() {
        return idle;
    }

    public void waitUntilConnected() {
        while(!publication.isConnected()) {
            idle.idle();
        }
    }

    public boolean waitUntilConnected(int retries) {
        AtomicInteger count = new AtomicInteger(0);
        while(count.get() < retries && !publication.isConnected()) {
            idle.idle();
            count.incrementAndGet();
        }
        return count.get() < retries;
    }

    @Override
    public void close() {
        if (!isNull(publication)) {
            publication.close();
        }
    }
}
