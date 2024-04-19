package org.producer;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.driver.MediaDriver;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.protocol.AeronContext;
import org.protocol.ConnectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


import static java.util.Objects.isNull;

public class Producer {
    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_IPC = "aeron:ipc";
    private static final int ALIGNMENT = 512;

    private final Aeron aeron;
    private final MediaDriver mediaDriver;
    private final IdleStrategy idle;

    private ExpandableDirectByteBuffer BUFFER = new ExpandableDirectByteBuffer(ALIGNMENT);
    private Publication publication;

    private Producer(Aeron aeron, MediaDriver mediaDriver, IdleStrategy idle, String streamUri, int streamId) {
        this.aeron = aeron;
        this.mediaDriver = mediaDriver;
        this.idle = idle;
        this.publication = aeron.addPublication(streamUri, streamId);
    }

    public static Producer initialize(String configPath) throws IOException {
        AeronContext aeronContext = AeronContext.getInstance();
        ConnectInfo connectInfo = ConnectInfo.readProducerFromFile(configPath);
        return new Producer(aeronContext.getAeron(), aeronContext.getMediaDriver(), connectInfo.getIdle(),
                connectInfo.getUri(), connectInfo.getStreamId());
    }

    public Publication getPublication() {
        return publication;
    }

    public ExpandableDirectByteBuffer getBuffer() {
        return BUFFER;
    }

    public IdleStrategy getIdle() {
        return idle;
    }
}
