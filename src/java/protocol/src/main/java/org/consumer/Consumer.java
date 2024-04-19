package org.consumer;

import java.io.IOException;
import java.util.ArrayList;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import org.agrona.concurrent.IdleStrategy;
import org.producer.Producer;
import org.protocol.AeronContext;
import org.protocol.ConnectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

public class Consumer {
    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
    private static final int ALIGNMENT = 512;

    private final Aeron aeron;
    private final MediaDriver mediaDriver;
    private final IdleStrategy idle;

    private Subscription subscription;

    private Consumer(Aeron aeron, MediaDriver mediaDriver, IdleStrategy idle, String streamUri, int streamId) {
        this.aeron = aeron;
        this.mediaDriver = mediaDriver;
        this.idle = idle;
        subscription = aeron.addSubscription(streamUri, streamId);
    }

    public static Consumer initializeOne(String configPath) throws IOException {
        AeronContext aeronContext = AeronContext.getInstance();
        ConnectInfo connectInfo = ConnectInfo.readConsumerFromFile(configPath).get(0);
        return new Consumer(aeronContext.getAeron(), aeronContext.getMediaDriver(),
                connectInfo.getIdle(), connectInfo.getUri(), connectInfo.getStreamId());
    }

    public static ArrayList<Consumer> initializeMany(String configPath) throws IOException {
        AeronContext aeronContext = AeronContext.getInstance();
        ArrayList<ConnectInfo> connectInfo = ConnectInfo.readConsumerFromFile(configPath);
        ArrayList<Consumer> consumers = new ArrayList<>();
        for (ConnectInfo conInfo : connectInfo) {
            consumers.add(new Consumer(aeronContext.getAeron(), aeronContext.getMediaDriver(),
                    conInfo.getIdle(), conInfo.getUri(), conInfo.getStreamId()));
        }
        return consumers;
    }

    public Subscription getSubsription() {
        return subscription;
    }

    public IdleStrategy getIdle() {
        return idle;
    }

    public boolean isConnected() {
        return subscription.isConnected();
    }
}