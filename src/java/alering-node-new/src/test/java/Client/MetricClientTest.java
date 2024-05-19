package Client;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.metrciclient.ClientMetricProducer;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricClientTest {

    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static int alertId = 0;
    private static int threshold = 200;

    private static Producer serverProducer;
    private static Consumer serverConsumer;
    private static Consumer alertConsumer;
    private static Consumer metricConsumer;

    private AlertNode alertNode;

    static void setupClient() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        Producer alertProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertProducerConfig.yaml"));
        Producer metricProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/MetricProducerConfig.yaml"));

        ClientMetricProducer clientMetricProducer = new ClientMetricProducer(metricProducer, alertProducer);
        Producer clientProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ClientProducerConfig.yaml"));
        Consumer clientConsumer = new Consumer(context, ConfigReader.readConsumerFromFile("src/test/resources/ClientConsumerConfig.yaml"));

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer);
        metricRegistry = MetricRegistry.getInstance();
    }

    static void setupServer() {
        alertConsumer = new Consumer(context, ConfigReader.readConsumerFromFile("src/test/resources/AlertConsumerConfig.yaml"));
        metricConsumer = new Consumer(context, ConfigReader.readConsumerFromFile("src/test/resources/MetricConsumerConfig.yaml"));

        serverProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ServerProducerConfig.yaml"));
        serverConsumer = new Consumer(context, ConfigReader.readConsumerFromFile("src/test/resources/ServerConsumerConfig.yaml"));
    }

    @BeforeAll
    static void run() throws IOException {
        setupClient();
        setupServer();
        metricRegistry.getClientProducer().waitUntilConnected();
        metricRegistry.getMetricProducer().waitUntilConnected();
        serverProducer.waitUntilConnected();
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
    public void test() throws InterruptedException {
        int alertMetricId = 12;
        String[] tags = {"ya", "south"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tags);
        metric.setAlertLogic(new GreaterAlert(new AlertInfo(alertId, alertMetricId, threshold)));
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
            poll = serverConsumer.getSubscription().poll(handler, 1000);
            serverConsumer.getIdle().idle();
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
            poll = alertConsumer.getSubscription().poll(alertHandler, 1000);
            alertConsumer.getIdle().idle();
        }
    }

}
