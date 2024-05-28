import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.DataBaseConnection;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiBalancer;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerTests {
    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static int alertId = 0;
    private static int threshold = 200;
    private static Producer alertProducer;
    private SqlClient client = DataBaseConnection.connect(5);

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
            work.doWork(client, serverProducer, buffer, offset, length, header);
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
            work.doWork(client, serverProducer, buffer, offset, length, header);
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

        GetMetricId work = new GetMetricId();
        work.addToMap(tagsList, alertMetricId);

        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            buffer.getInt(offset); // вытаскиваем id инструкции
            offset += MetricConstants.INT_SIZE;
            work.doWork(client, serverProducer, buffer, offset, length, header);
        };

        new Thread(() -> {
            int poll = -1;
            while (poll <= 0) {
                poll = serverSubscriber.getSubscription().poll(handler, 1000);
                serverSubscriber.getIdle().idle();
            }
        }).start();

        int metricsCount = 10000;
        // отправили запрос на metricId
        long startRequest = System.nanoTime();
        ArrayList<Long >startAlert = new ArrayList<>(metricsCount);
        ArrayList<Long> endAlert = new ArrayList<>(metricsCount);
        Metric metric = metricRegistry.getMetric(tagsList);
        metric.setAlertProducer(alertProducer);
        metric.addAlertLogic(new GreaterAlert(new AlertInfo(alertId, alertMetricId,  threshold)));
        long alertValue1 = 13, alertValue2 = 300;
        long alertTimestamp1 = 100, alertTimestamp2 = 100;
        AtomicInteger curAlert = new AtomicInteger(0);
        new Thread(() -> {
            FragmentHandler alertHandler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                int alertId = buffer.getInt(offset);
                offset += MetricConstants.ID_SIZE;
                int metricId = buffer.getInt(offset);
                offset += MetricConstants.ID_SIZE;
                long value = buffer.getLong(offset);
                offset += MetricConstants.LONG_SIZE;
                long timestamp = buffer.getLong(offset);
                offset += MetricConstants.LONG_SIZE;
                int curAlertId = curAlert.getAndIncrement();
                assertEquals(metric.getMetricId(), metricId);
//                assertEquals(value, curAlertId + threshold + 1);
//                assertEquals(timestamp, alertTimestamp2 + curAlertId);
                endAlert.add(System.nanoTime());
                // получили крит алерт
            };
            int poll = -1;
            while (poll <= 0 || curAlert.get() < metricsCount) {
                poll = alertSubscriber.getSubscription().poll(alertHandler, 100000);
                alertSubscriber.getIdle().idle();
            }
        }).start();

        for (int i = 0; i < metricsCount; ++i) {
            metric.addValue(i + threshold + 1, alertTimestamp1++);
            startAlert.add(System.nanoTime());
        }
//        metric.addValue(alertValue1, alertTimestamp1);
        // отправили метрику для крит алерта
//        metric.addValue(alertValue2, alertTimestamp2);
//        assertEquals(metric.getMetricId(), -1);
        Thread.sleep(10);
        MetricRegistry temp = MetricRegistry.getInstance();
        assertEquals(metric.getMetricId(), alertMetricId);
        long endRequest = System.nanoTime();
        // Получили ответ на запрос
        ArrayList<Long> latencies = new ArrayList<>(metricsCount);
        while (curAlert.get() < metricsCount) {
            Thread.sleep(20);
        }
        long maxtemp = -1;
        for (int i = 0; i < metricsCount; ++i) {
            if (maxtemp < endAlert.get(i) - startAlert.get(i)) {
                maxtemp = endAlert.get(i) - startAlert.get(i);
            }
            latencies.add(endAlert.get(i) - startAlert.get(i));
        }
        System.out.println("Request response time: " + getMicroLatency(endRequest - startRequest) + " mcs");
        latencies.sort(Comparator.naturalOrder());
        System.out.println(String.format("Fastest - %s mcs", getMicroLatency(latencies.getFirst())));
        System.out.println(String.format("0.5 latency - %s mcs", getLatencyPercentile(latencies, 0.5)));
        System.out.println(String.format("0.9 latency - %s mcs", getLatencyPercentile(latencies, 0.9)));
        System.out.println(String.format("0.95 latency - %s mcs", getLatencyPercentile(latencies, 0.95)));
        System.out.println(String.format("0.99 latency - %s mcs", getLatencyPercentile(latencies, 0.99)));
        System.out.println(String.format("0.9999 latency - %s mcs", getLatencyPercentile(latencies, 0.9999)));
        System.out.println(maxtemp);
//        System.out.println(String.format("1.0 latency - %s mcs", getLatencyPercentile(latencies, 1.0)));


    }


    private double getLatencyPercentile(List<Long> nanoLatencies, double percentile) {
        var index = (int) (percentile * nanoLatencies.size());
        var nanoLatency = nanoLatencies.get(index);
        return getMicroLatency(nanoLatency);
    }

    private double getMicroLatency(Long nanoLatency) {
        var microLatency = nanoLatency / 1000.0;
        return new BigDecimal(microLatency)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
