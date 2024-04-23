package ru.realalerting.producer;

import io.aeron.Publication;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import ru.realalerting.protocol.AeronContext;
import ru.realalerting.protocol.ConnectInfo;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;


/**
 * @author Karbayev Saruar
 */
public class Producer implements AutoCloseable {

    private final Publication publication;
    private final IdleStrategy idle;

    public Producer(AeronContext aeronContext, ConnectInfo connectInfo) {
        this.publication = aeronContext.getAeron().addPublication(connectInfo.getUri(), connectInfo.getStreamId());
        idle = new SleepingIdleStrategy();
    }

    public Producer(AeronContext aeronContext, ConnectInfo connectInfo, IdleStrategy idleStrategy) {
        this.publication = aeronContext.getAeron().addPublication(connectInfo.getUri(), connectInfo.getStreamId());
        idle = idleStrategy;
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
