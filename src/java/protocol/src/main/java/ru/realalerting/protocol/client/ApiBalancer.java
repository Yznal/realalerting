package ru.realalerting.protocol.client;

import org.agrona.concurrent.AgentRunner;
import ru.realalerting.producer.Producer;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiBalancer {
//    private int apiNodeMax = 10;
//    private ApiNode[] apiNodes = new ApiNode[apiNodeMax];
//    private ConcurrentHashMap<Integer, Integer> apiNodeByClientId = new ConcurrentHashMap<>();
//    private AtomicInteger curFreeNode = new AtomicInteger();
//
//
//
//    public ApiBalancer() {
//        for (int i = 0; i < apiNodeMax; i++) {
//            apiNodes[i] = new ApiNode();
//        }
//    }
//
//    public void setNodeToClient(int clientId) {
//        // TODO проверять что не вышли за пределы
//        int curFreeNodeIndex = curFreeNode.getAndAdd(2);
//        apiNodeByClientId.putIfAbsent(2 * clientId, curFreeNodeIndex);
//        apiNodeByClientId.putIfAbsent(2 * clientId + 1, curFreeNodeIndex+1);
//    }
//
//    public void assignProducerAndSubscriberToApiNode(int clientId, Producer producer1, Producer producer2, Subscriber subscriber) {
//        ApiNode node1 = apiNodes[apiNodeByClientId.get(2 * clientId)];
//        ApiNode node2 = apiNodes[apiNodeByClientId.get(2 * clientId + 1)];
//        node1.setApiRequestSubscriber(subscriber);
//        node2.setApiRequestSubscriber(subscriber);
//        node1.setApiResponseProducer(producer1);
//        node2.setApiResponseProducer(producer2);
//    }
//
//    public void startApiNodes(int clientId) {
//        ApiNode node1 = apiNodes[apiNodeByClientId.get(2 * clientId)];
//        ApiNode node2 = apiNodes[apiNodeByClientId.get(2 * clientId + 1)];
//        final AgentRunner receiveAgentRunner1 = new AgentRunner(node1.getApiRequestSubscriber().getIdle(), Throwable::printStackTrace, null, node1);
//        AgentRunner.startOnThread(receiveAgentRunner1);
//        final AgentRunner receiveAgentRunner2 = new AgentRunner(node2.getApiRequestSubscriber().getIdle(), Throwable::printStackTrace, null, node2);
//        AgentRunner.startOnThread(receiveAgentRunner2);
//    }
//
//    public void waitUntilConnected(int clientId) {
//        ApiNode node1 = apiNodes[apiNodeByClientId.get(2 * clientId)];
//        ApiNode node2 = apiNodes[apiNodeByClientId.get(2 * clientId + 1)];
//        node1.waitUntilConnected();
//        node2.waitUntilConnected();
//    }



}
