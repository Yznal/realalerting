package ru.realalerting.protocol;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.subscriber.Subscriber;

public class ClientProtocolConnection implements FragmentHandler, AutoCloseable, Agent {
    private final int clientId;
    private final Producer apiResponseProducer;
    private final Subscriber apiRequestSubscriber;
    private GetMetricId getMetricId;
    private SqlClient database;
    private int maxFragmentSize = 1000;

    public ClientProtocolConnection(int clientId, Producer apiResponseProducer, Subscriber apiRequestSubscriber,
                                    GetMetricId getMetricId, SqlClient database) {
        this.clientId = clientId;
        this.apiResponseProducer = apiResponseProducer;
        this.apiRequestSubscriber = apiRequestSubscriber;
        this.getMetricId = getMetricId;
        this.database = database;
    }

    public ClientProtocolConnection(int clientId, Producer apiResponseProducer, Subscriber apiRequestSubscriber,
                                    GetMetricId getMetricId, SqlClient database, int maxFragmentSize) {
        this.clientId = clientId;
        this.apiResponseProducer = apiResponseProducer;
        this.apiRequestSubscriber = apiRequestSubscriber;
        this.getMetricId = getMetricId;
        this.database = database;
        this.maxFragmentSize = maxFragmentSize;
    }

    public Producer getApiResponseProducer() {
        return apiResponseProducer;
    }

    public int getClientId() {
        return clientId;
    }

    public Subscriber getApiRequestSubscriber() {
        return apiRequestSubscriber;
    }

    public void waitUntilConnected() {
        if (apiResponseProducer != null) {
            apiResponseProducer.waitUntilConnected();
        }
//        apiRequestSubscriber.waitUntilConnected();
    }

    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        int instructionId = buffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        switch (instructionId) {
            case Protocol.INSTRUCTION_GET_METRIC_ID_BY_TAGS -> {
                getMetricId.doWork(database, apiResponseProducer, buffer, offset, length, header);
            } // TODO + 2 инструкции
            case Protocol.INSTRUCTION_GET_METRIC_CRITICAL_ALERTS_BY_METRIC_ID -> {

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
        apiRequestSubscriber.getSubscription().poll(this, maxFragmentSize);
        return 0;
    }

    @Override
    public String roleName() {
        return clientId + " Api connection";
    }
}
