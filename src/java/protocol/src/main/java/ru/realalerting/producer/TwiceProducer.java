package ru.realalerting.producer;

import io.aeron.logbuffer.BufferClaim;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

public class TwiceProducer extends BaseProducer{
    private Producer secondProducer;
    private final ThreadLocal<BufferClaim> secondBufferClaim = ThreadLocal.withInitial(() -> new BufferClaim());


    public TwiceProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        super(aeronContext, streamId, isIpc);
        secondProducer = new Producer(aeronContext, new RealAlertingConfig(streamId, isIpc));
    }

    public TwiceProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        super(aeronContext, uri, streamId);
        secondProducer = new Producer(aeronContext, new RealAlertingConfig(uri, streamId));
    }

    public TwiceProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(aeronContext, connectInfo);
        secondProducer = new Producer(aeronContext, connectInfo);
    }

    public TwiceProducer(Producer firstProducer, Producer secondProducer) {
        super(firstProducer);
        this.secondProducer = secondProducer;
    }



}
