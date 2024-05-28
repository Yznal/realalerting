package ru.realalerting.protocol.client;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.ClientProtocolConnection;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;
import ru.realalerting.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiNode {
    private Int2ObjectOpenHashMap<ClientProtocolConnection> connections = new Int2ObjectOpenHashMap<>();
    private Int2ObjectOpenHashMap<AgentRunner> runners = new Int2ObjectOpenHashMap<>();
    private GetMetricId getMetricId = new GetMetricId();
    private SqlClient database;

    public ApiNode() {}

    public ApiNode(SqlClient database) {
        this.database = database;
    }

    public void setDatabase(SqlClient database) {
        this.database = database;
    }

    public void addClient(int clientId, Producer producer, Subscriber subscriber) {
        connections.put(clientId, new ClientProtocolConnection(clientId, producer, subscriber, getMetricId, database));
    }

    public void startClient(int clientId) {
        ClientProtocolConnection connection = connections.get(clientId);
        if (connection != null) {
            connection.waitUntilConnected();
            final AgentRunner receiveAgentRunner = new AgentRunner(connection.getApiRequestSubscriber().getIdle(), Throwable::printStackTrace, null, connection);
            runners.put(clientId, receiveAgentRunner);
            AgentRunner.startOnThread(receiveAgentRunner);
        }
    }

}
