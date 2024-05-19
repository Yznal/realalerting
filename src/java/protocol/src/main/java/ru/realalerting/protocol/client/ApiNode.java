package ru.realalerting.protocol.client;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;
import ru.realalerting.subscriber.Subscriber;

import java.util.List;

public class ApiNode implements FragmentHandler, AutoCloseable, Agent {
    private Producer apiResponseProducer;
    private Subscriber apiRequestSubscriber;
    private int maxFragments = 1000;
    private GetMetricId getMetricId = new GetMetricId();

    public ApiNode() {

    }

    public ApiNode(Producer apiResponseProducer, Subscriber apiRequestSubscriber) {
        this.apiResponseProducer = apiResponseProducer;
        this.apiRequestSubscriber = apiRequestSubscriber;
    }

    Subscriber getApiRequestSubscriber() {
        return apiRequestSubscriber;
    }

    public void setApiResponseProducer(Producer apiResponseProducer) {
        this.apiResponseProducer = apiResponseProducer;
    }

    public void setApiRequestSubscriber(Subscriber apiRequestSubscriber) {
        this.apiRequestSubscriber = apiRequestSubscriber;
    }

    public void waitUntilConnected() {
        if (apiResponseProducer != null) {
            apiResponseProducer.waitUntilConnected();
        }
//        apiRequestSubscriber.waitUntilConnected();
    }

//    public void addToMap(List<CharSequence> tags, int metricId) { // TODO удалить функцию после того как подключим ControlPlane
//        getMetricId.getIdByTags().put(tags, metricId);
//    }


    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        int instructionId = buffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        switch (instructionId) {
            case Protocol.INSTRUCTION_GET_METRIC_ID -> {
                getMetricId.doWork(apiResponseProducer, buffer, offset, length, header);
            }
            default -> throw new IllegalStateException("Invalid instruction id in api node: " + instructionId);
        }
    }

    @Override
    public void close() throws Exception {
        if (apiRequestSubscriber != null) {
            apiResponseProducer.close();
        }
        if (apiResponseProducer != null) {
            apiRequestSubscriber.close();
        }

    }

    @Override
    public int doWork() throws Exception {
        apiRequestSubscriber.getSubscription().poll(this, maxFragments);
        return 0;
    }

    @Override
    public String roleName() {
        return "Api server";
    }
}
