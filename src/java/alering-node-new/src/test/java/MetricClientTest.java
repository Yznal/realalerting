import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.alertnode.AlertSystemBalancer;
import ru.realalerting.alertsubscriber.AlertSubscriber;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricClientTest {

    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static int alertId = 6;
    private static int metricId = 15;
    private static int threshold = 200;
    private static long alertValue = 300;
    private static long alertTimestamp = 101;

    private static AlertSubscriberTest alertSubscriber;

//    private static Subscriber alertSubscriber;

    private static Producer alertNodeProducer1;
    private static Producer alertNodeProducer2;
    private static Subscriber alertNodeSubscriber1;
    private static Subscriber alertNodeSubscriber2;

    private static Producer serverProducer;
    private static Subscriber serverSubscriber;

    // alertSubscriber - subscriber
    // 2 AlertNode - 2 * producer + 2 subscriber
    //
    // for requests - producer + subscriber
    // for metrics - producer

    static void setupClient() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        Producer metricProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/MetricProducerConfig.yaml"));

        MetricProducer clientMetricProducer = new MetricProducer(metricProducer);
        Producer clientProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ClientProducerConfig.yaml"));
        Subscriber clientConsumer = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ClientConsumerConfig.yaml"));

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer, context);
        metricRegistry = MetricRegistry.getInstance();
    }

    static void setupServer() {
        alertSubscriber = new AlertSubscriberTest(new Subscriber(context,
                ConfigReader.readConsumerFromFile("src/test/resources/AlertConsumerConfig.yaml")),
                alertId, metricId, alertValue, alertTimestamp);
        alertSubscriber.start();


        serverProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ServerProducerConfig.yaml"));
        serverSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ServerConsumerConfig.yaml"));

        alertNodeProducer1 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeProducerConfig1.yaml"));
        alertNodeProducer2 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeProducerConfig2.yaml"));
        alertNodeSubscriber1 = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/AlertNodeConsumerConfig1.yaml"));
        alertNodeSubscriber2 = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/AlertNodeConsumerConfig2.yaml"));
    }

    @BeforeAll
    static void run() throws IOException {
        setupClient();
        setupServer();
        metricRegistry.getClientProducer().waitUntilConnected();
        metricRegistry.getMetricProducer().waitUntilConnected();
        serverProducer.waitUntilConnected();
        alertNodeProducer1.waitUntilConnected();
        alertNodeProducer2.waitUntilConnected();
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
    public void test() throws Exception {
        int clientId = 0;
        String[] tags = {"ya", "south"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);

        AlertSystemBalancer alertSystemBalancer = new AlertSystemBalancer();
        alertSystemBalancer.clientCreated(clientId);
        alertSystemBalancer.assignClientToAlertNode(clientId, alertNodeProducer1, alertNodeProducer2,
                alertNodeSubscriber1, alertNodeSubscriber2);
        AlertNode[] alertNodes = alertSystemBalancer.getAlertNodesByClientId(clientId);
        AlertInfo alertInfo = new AlertInfo(alertId, metricId, threshold);
        for (int i = 0; i < 2; ++i) {
            alertNodes[i].addAlert(alertInfo, new GreaterAlert(alertInfo));
            alertNodes[i].start();
        }


        long alertValue1 = 13, alertValue2 = alertValue;
        long alertTimestamp1 = 100, alertTimestamp2 = alertTimestamp;
        metric.addValue(alertValue1, alertTimestamp1);
        metric.addValue(alertValue2, alertTimestamp2);
        assertEquals(metric.getMetricId(), -1);
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
        MetricRegistry temp = MetricRegistry.getInstance();
        assertEquals(metric.getMetricId(), metricId);
        while (!alertSubscriber.isAlertArrived()) {
            Thread.sleep(10);
        }
        assertEquals(alertSubscriber.isAlertArrived(), true);
        assertEquals(alertSubscriber.getAlertCount(), 1);
    }

}
