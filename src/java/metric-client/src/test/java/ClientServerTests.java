import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiBalancer;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerTests {
    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static int alertId = 0;
    private static int threshold = 200;
    private static Producer alertProducer;

    private static Producer serverProducer;
    private static Subscriber serverSubscriber;
    private static Subscriber alertSubscriber;
    private static Subscriber metricSubscriber;

    static void setupClient() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        alertProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertProducerConfig.yaml"));
        Producer metricProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/MetricProducerConfig.yaml"));

        MetricProducer clientMetricProducer = new MetricProducer(metricProducer);
        Producer clientProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ClientProducerConfig.yaml"));
        Subscriber clientConsumer = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ClientConsumerConfig.yaml"));

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer, context);
        metricRegistry = MetricRegistry.getInstance();
    }

    static void setupServer() {
        alertSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/AlertConsumerConfig.yaml"));
        metricSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/MetricConsumerConfig.yaml"));

        serverProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ServerProducerConfig.yaml"));
        serverSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ServerConsumerConfig.yaml"));
    }

    @BeforeAll
    static void run() throws IOException {
        setupClient();
        setupServer();
        metricRegistry.getClientProducer().waitUntilConnected();
        metricRegistry.getMetricProducer().waitUntilConnected();
        serverProducer.waitUntilConnected();
    }


    @Test
    public void testGettingMetricId() throws InterruptedException {
        int metricId = 10;
        String[] tags = {"ya", "west"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);
        metric.setAlertProducer(alertProducer);
        GetMetricId work = new GetMetricId();
        work.addToMap(tagsList, metricId);
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            buffer.getInt(offset); // вытаскиваем id инструкции
            offset += MetricConstants.INT_SIZE;
            work.doWork(serverProducer, buffer, offset, length, header);
        };
        int poll = -1;
        while (poll <= 0) {
            poll = serverSubscriber.getSubscription().poll(handler, 1000);
            serverSubscriber.getIdle().idle();
        }
        Thread.sleep(20);
        assertEquals(metric.getMetricId(), metricId);
    }

    private class MetricValues {
        long value;
        long timestamp;

        public MetricValues(long value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public long getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    @Test
    public void testSendMetric() throws InterruptedException {
        int alertMetricId = 11;
        String[] tags = {"ya", "north"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);
        metric.setAlertProducer(alertProducer);
        ArrayList<MetricValues> sendMetrics = new ArrayList<>();
        long curMetricValue = 1;
        long curTimestamp = 100;
        long metricsCount = 1000;
        for (int i = 0; i < metricsCount; ++i) {
            metric.addValue(curMetricValue, curTimestamp);
            sendMetrics.add(new MetricValues(curMetricValue++, curTimestamp++));
        }
        assertEquals(metric.getMetricId(), -1);

        ApiBalancer apiBalancer = new ApiBalancer();
        apiBalancer.setNodeToClient(0);


        GetMetricId work = new GetMetricId();
        work.addToMap(tagsList, alertMetricId);
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            buffer.getInt(offset); // вытаскиваем id инструкции
            offset += MetricConstants.INT_SIZE;
            work.doWork(serverProducer, buffer, offset, length, header);
        };
        int poll = -1;
        while (poll <= 0) {
            poll = serverSubscriber.getSubscription().poll(handler, 1000);
            serverSubscriber.getIdle().idle();
        }
        Thread.sleep(20);
        MetricRegistry temp = MetricRegistry.getInstance();
        assertEquals(metric.getMetricId(), alertMetricId);

        AtomicInteger curMetricIndex = new AtomicInteger();

        FragmentHandler metricHandler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            int metricId = buffer.getInt(offset);
            offset += MetricConstants.INT_SIZE;
            long value = buffer.getLong(offset);
            offset += MetricConstants.LONG_SIZE;
            long timestamp = buffer.getLong(offset);
            offset += MetricConstants.LONG_SIZE;
            assertEquals(metric.getMetricId(), metricId);
            assertEquals(value, sendMetrics.get(curMetricIndex.get()).getValue());
            assertEquals(timestamp, sendMetrics.get(curMetricIndex.getAndIncrement()).getTimestamp());
        };
        poll = -1;
        metricRegistry.getMetricProducer();
        while (poll <= 0) {
            poll = metricSubscriber.getSubscription().poll(metricHandler, 1000);
            metricSubscriber.getIdle().idle();
        }
        assertEquals(curMetricIndex.get(), metricsCount);

    }

    @Test
    public void testSendAlert() throws InterruptedException {
        int alertMetricId = 12;
        String[] tags = {"ya", "south"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);
        metric.setAlertProducer(alertProducer);
        metric.setAlertLogic(new GreaterAlert(new AlertInfo(alertId, alertMetricId,  threshold)));
        long alertValue1 = 13, alertValue2 = 300;
        long alertTimestamp1 = 100, alertTimestamp2 = 101;
        metric.addValue(alertValue1, alertTimestamp1);
        metric.addValue(alertValue2, alertTimestamp2);
        assertEquals(metric.getMetricId(), -1);
        GetMetricId work = new GetMetricId();
        work.addToMap(tagsList, alertMetricId);
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            buffer.getInt(offset); // вытаскиваем id инструкции
            offset += MetricConstants.INT_SIZE;
            work.doWork(serverProducer, buffer, offset, length, header);
        };
        int poll = -1;
        while (poll <= 0) {
            poll = serverSubscriber.getSubscription().poll(handler, 1000);
            serverSubscriber.getIdle().idle();
        }
        Thread.sleep(20);
        MetricRegistry temp = MetricRegistry.getInstance();
        assertEquals(metric.getMetricId(), alertMetricId);

        FragmentHandler alertHandler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            int metricId = buffer.getInt(offset);
            offset += MetricConstants.INT_SIZE;
            long value = buffer.getLong(offset);
            offset += MetricConstants.LONG_SIZE;
            long timestamp = buffer.getLong(offset);
            offset += MetricConstants.LONG_SIZE;
            assertEquals(metric.getMetricId(), metricId);
            assertEquals(value, alertValue2);
            assertEquals(timestamp, alertTimestamp2);
        };
        poll = -1;
        while (poll <= 0) {
            poll = alertSubscriber.getSubscription().poll(alertHandler, 1000);
            alertSubscriber.getIdle().idle();
        }
    }
}
