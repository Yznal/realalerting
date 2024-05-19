package ru.realalerting.metrciclient;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;
import ru.realalerting.subscriber.BaseSubscriber;
import ru.realalerting.subscriber.Subscriber;

public class ClientSubscriber extends BaseSubscriber {
    private final MetricProcessor metricProcessor = new MetricProcessor();
    private int maxFragment = 1000;

    public ClientSubscriber(Subscriber consumer) {
        super(consumer);
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        int instructionId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        switch (instructionId) {
            case Protocol.INSTRUCTION_SET_METRIC_ID -> {
                int[] ids = metricProcessor.setMetricId(directBuffer, offset, length, header);
                MetricRegistry.getInstance().changeMetric(ids[0], ids[1]);
            }
            default -> throw new IllegalStateException("Invalid instruction id: " + instructionId);
        }
    }

    @Override
    public int doWork() {
        subscriber.getSubscription().poll(this, maxFragment);
        return 0;
    }

    @Override
    public String roleName() {
        return "client side commands receiver";
    }
}
