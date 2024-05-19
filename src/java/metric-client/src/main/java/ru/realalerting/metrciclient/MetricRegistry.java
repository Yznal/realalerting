package ru.realalerting.metrciclient;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.agrona.concurrent.AgentRunner;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.subscriber.Subscriber;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricRegistry implements AutoCloseable {
    private AtomicInteger requsetId = new AtomicInteger(0); // они всегда отрицательные
    private Int2ObjectOpenHashMap<Object> requestContexts = new Int2ObjectOpenHashMap<>();
    private Int2ObjectOpenHashMap<Metric> metricByIds = new Int2ObjectOpenHashMap<>();
    private ConcurrentHashMap<List<CharSequence>, Metric> metricByTags = new ConcurrentHashMap<>();
    private ClientProducer clientProducer;
    private ClientSubscriber clientSubscriber;
    private MetricProducer metricProducer;
    private static MetricRegistry registry = null;
    private final RealAlertingDriverContext driverContext;


    public static MetricRegistry getInstance() {
        if (registry == null) {
            throw new IllegalStateException("MetricRegistry not initialized");
        }
        return registry;
    }

    public static void initialize(Producer producer, Subscriber subscriber, MetricProducer metricProducer,
                                  RealAlertingDriverContext driverContext) {
        if (registry == null) {
            registry = new MetricRegistry(producer, subscriber, metricProducer, driverContext);
        }
    }

    private MetricRegistry(Producer producer, Subscriber subscriber, MetricProducer metricProducer,
                           RealAlertingDriverContext driverContext) {
        this.clientProducer = new ClientProducer(producer);
        this.clientSubscriber = new ClientSubscriber(subscriber);
        this.metricProducer = metricProducer;
        this.driverContext = driverContext;
        final AgentRunner receiveAgentRunner = new AgentRunner(subscriber.getIdle(), Throwable::printStackTrace, null, clientSubscriber);
        AgentRunner.startOnThread(receiveAgentRunner);
    }

    public ClientProducer getClientProducer() {
        return clientProducer;
    }

    public MetricProducer getMetricProducer() {
        return metricProducer;
    }

    RealAlertingDriverContext getDriverContext() {
        return driverContext;
    }

    public Metric getMetric(List<CharSequence> tags) {
        Metric gettedMetric = metricByTags.get(tags);
        if (gettedMetric != null) {
            return gettedMetric;
        }
        gettedMetric = new Metric(getInstance());
        Metric metric = metricByTags.putIfAbsent(tags, gettedMetric);
        if (metric != null) {
            gettedMetric = metric;
        } else {
            int curRequestId = requsetId.getAndIncrement();
            requestContexts.put(curRequestId, tags);
            clientProducer.getMetricId(curRequestId, tags); // TODO what if not sent
        }
        return gettedMetric;
    }

    public Metric getMetric(int metricId) {
        Metric gettedMetric = metricByIds.get(metricId);
        if (gettedMetric != null) {
            return gettedMetric;
        }
        Metric metric = new Metric(getInstance(), metricId);
        metricByIds.put(metricId, metric); // TODO Actor для записи в Map
        return metric;
    }

    void changeMetric(int requestId, int newId) {
        List<CharSequence> gettedTags = (List<CharSequence>) requestContexts.get(requestId);
        if (gettedTags != null) {
            metricByTags.get(gettedTags).changeId(newId);
            requestContexts.remove(requestId); // дедупликация
        }
    }

    @Override
    public void close() throws Exception {
            clientSubscriber.close();
            clientProducer.close();
            metricProducer.close();
            clientSubscriber.close();
    }
}
