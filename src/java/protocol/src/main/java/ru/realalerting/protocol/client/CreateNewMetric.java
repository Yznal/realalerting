package ru.realalerting.protocol.client;

import ru.realalerting.subscriber.Subscriber;
import ru.realalerting.producer.Producer;

import java.util.HashMap;


/**
 * @author Karbayev Saruar
 */
public final class CreateNewMetric {
    private Producer producer;
    private Subscriber subscriber;

    public void doWork(int requestId, HashMap<String, String> tags) {

    }
}
