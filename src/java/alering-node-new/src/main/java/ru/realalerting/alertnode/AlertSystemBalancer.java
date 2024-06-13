package ru.realalerting.alertnode;

import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertSystemBalancer {
//    int maxAlertNodesSize = 50;
//    public AlertNode[] alertNodes = new AlertNode[2 * maxAlertNodesSize];
//    private ConcurrentHashMap<Integer, Integer> alertNodeIdByClientId = new ConcurrentHashMap<>();
//    private AtomicInteger curFreeNode = new AtomicInteger();
//
//    public AlertSystemBalancer() {
//        for (int i = 0; i < maxAlertNodesSize; i++) {
//            alertNodes[i] = new AlertNode();
//        }
//    }
//
//    public void clientCreated(int clientId) {
//        int curFreeNodeId = curFreeNode.getAndAdd(2);
//        alertNodeIdByClientId.put(2 * clientId, curFreeNodeId);
//        alertNodeIdByClientId.put(2 * clientId + 1, curFreeNodeId + 1);
//    }
//
//    public void assignClientToAlertNode(int clientId, Producer producer1, Producer producer2,
//                                        Subscriber subscriber1, Subscriber subscriber2) {
//        AlertNode node1 = alertNodes[alertNodeIdByClientId.get(2 * clientId)];
//        AlertNode node2 = alertNodes[alertNodeIdByClientId.get(2 * clientId + 1)];
//        node1.setAlertProducer(producer1);
//        node2.setAlertProducer(producer2);
//        node1.setMetricSubscriber(subscriber1);
//        node2.setMetricSubscriber(subscriber2);
//    }
//
//    public AlertNode[] getAlertNodesByClientId(int clientId) {
//        return new AlertNode[]{alertNodes[alertNodeIdByClientId.get(2 * clientId)],
//                alertNodes[alertNodeIdByClientId.get(2 * clientId + 1)]};
//    }
//
//    public void addAlertToClientsAlertNodes(int clientId, AlertInfo alertInfo, AlertLogicBase alertLogic) {
//        AlertNode node1 = alertNodes[alertNodeIdByClientId.get(2 * clientId)];
//        AlertNode node2 = alertNodes[alertNodeIdByClientId.get(2 * clientId + 1)];
//        node1.addAlert(alertInfo, alertLogic);
//        node2.addAlert(alertInfo, alertLogic);
//    }
//
//    public void startClientsAlertNodes(int clientId) throws Exception {
//        AlertNode node1 = alertNodes[alertNodeIdByClientId.get(2 * clientId)];
//        AlertNode node2 = alertNodes[alertNodeIdByClientId.get(2 * clientId + 1)];
//        node1.start();
//        node2.start();
//    }
}
