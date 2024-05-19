package ru.realalerting.alertnode;

import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertSystemBalancer {
    int maxAlertNodesSize = 50;
    public AlertNode[] alertNodes = new AlertNode[2 * maxAlertNodesSize];
    private ConcurrentHashMap<Integer, Integer> alertNodeByClientId = new ConcurrentHashMap<>();
    private AtomicInteger curFreeNode = new AtomicInteger();

    public void assignClinetToAlertNode(int clientId, Producer producer1, Producer producer2, Subscriber subscriber) {
        AlertNode node1 = alertNodes[alertNodeByClientId.get(2 * clientId)];
        AlertNode node2 = alertNodes[alertNodeByClientId.get(2 * clientId + 1)];
        node1.setAlertProducer(producer1);
        node2.setAlertProducer(producer2);
        node1.setMetricSubscriber(subscriber);
        node2.setMetricSubscriber(subscriber);
    }
}
