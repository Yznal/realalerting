import io.vertx.sqlclient.SqlClient;
import ru.realalerting.alertsubscriber.AlertSubscriber;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientRunning {

    private static int alertCount = 3;
    private static AtomicInteger responseAlertCount = new AtomicInteger(0);
    private static ArrayList<AlertSubscriber> alertSubscribers = new ArrayList<>();
    private static ConcurrentHashMap<Integer, ArrayList<Long>> timestampsByAlertId = new ConcurrentHashMap<>();
    private static ArrayList<Long> latencies = new ArrayList<>();

    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_MDC_PUBLICATION = "aeron:udp?control-mode=dynamic|control=%s:%s";
    private static final String AERON_MDC_SUBSCRIPTION = "aeron:udp?endpoint=%s:%s|control=%s:%s|control-mode=dynamic";

    private static ArrayList<Long> timestamps = new ArrayList<>();

    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;

    private static final Random RANDOM = new Random(42);

    private static void setUpMetricRegistry() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");

        Producer metricProducer = new Producer(context, String.format(AERON_MDC_PUBLICATION, "localhost", 6006), 4); // "src/test/resources/Running/MetricProducerConfig.yaml"

        MetricProducer clientMetricProducer = new MetricProducer(metricProducer);
        Producer clientProducer = new Producer(context, String.format(AERON_UDP_FORMAT, "localhost", 6001), 1); // "src/test/resources/Running/ClientProducerConfig.yaml"
        Subscriber clientConsumer = new Subscriber(context, String.format(AERON_UDP_FORMAT, "localhost", 6000), 2); // "src/test/resources/Running/ClientConsumerConfig.yaml"

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer, context);
        metricRegistry = MetricRegistry.getInstance();

        metricRegistry.getClientProducer().waitUntilConnected();
        metricRegistry.getMetricProducer().waitUntilConnected();
    }

    private static void setUpAlertSubscriber() {
        for (int i = 1; i <= alertCount; ++i) {
            Subscriber subscriber = new Subscriber(context, String.format(AERON_MDC_SUBSCRIPTION, "localhost", 6020, "localhost", 6010), i); // "src/test/resources/Running/AlertSubscribers/AlertConsumerConfig" + i + "_1" + ".yaml"
            AlertSubscriber alertSubscriber = new AlertSubscriber(subscriber/*, 100 + i*/) {
                @Override
                public void onAlert(int alertId, int metricId, long value, long timestamp) {
                    System.out.println(alertId);
                    System.out.println(responseAlertCount.incrementAndGet());
                    timestampsByAlertId.putIfAbsent(alertId, new ArrayList<>());
                    timestampsByAlertId.get(alertId).add(System.nanoTime());
                }
            };
            alertSubscriber.start();
            alertSubscribers.add(alertSubscriber);
        }
    }

    public static void setUp() throws IOException {
        setUpMetricRegistry();
        setUpAlertSubscriber();
    }

    public static void runClient() throws InterruptedException {
        String[] tags1 = {"ya", "west101"};
        List<CharSequence> tagsList1 = new ArrayList<CharSequence>(Arrays.asList(tags1));
        String[] tags2 = {"ya", "west102"};
        List<CharSequence> tagsList2 = new ArrayList<CharSequence>(Arrays.asList(tags2));
        String[] tags3 = {"ya", "west103"};
        List<CharSequence> tagsList3 = new ArrayList<CharSequence>(Arrays.asList(tags3));
        String[] tags4 = {"ya", "west104"};
        List<CharSequence> tagsList4 = new ArrayList<CharSequence>(Arrays.asList(tags4));
        String[] tags5 = {"ya", "west105"};
        List<CharSequence> tagsList5 = new ArrayList<CharSequence>(Arrays.asList(tags5));
        Metric metric1 = metricRegistry.getMetric(tagsList1);
        Metric metric2 = metricRegistry.getMetric(tagsList2);
        Metric metric3 = metricRegistry.getMetric(tagsList3);
        Metric metric4 = metricRegistry.getMetric(tagsList4);
        Metric metric5 = metricRegistry.getMetric(tagsList5);

        while(metric1.getMetricId() == -1) {
            Thread.sleep(50);
        }
        while(metric2.getMetricId() == -1) {
            Thread.sleep(50);
        }
        while(metric3.getMetricId() == -1) {
            Thread.sleep(50);
        }
        while(metric4.getMetricId() == -1) {
            Thread.sleep(50);
        }
        while(metric5.getMetricId() == -1) {
            Thread.sleep(50);
        }
//        Thread.sleep(1000);

        for (int i = 0; i < 10000; ++i) {
            if (i >= 1) {
                timestamps.add(System.nanoTime());
            }
            metric1.addValue(i, i);
            metric2.addValue(i, i);
            metric3.addValue(i, i);
            metric4.addValue(i, i);
            metric5.addValue(i, i);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        setUp();
        runClient();
//        while(true) {
//            Thread.sleep(1000);
//        }

        Thread.sleep(10 * 1000);

        for (Map.Entry<Integer, ArrayList<Long>> e: timestampsByAlertId.entrySet()) {
            int i = 0;
            for (Long timestamp : e.getValue()) {
                latencies.add(timestamp - timestamps.get(i++));
            }
        }
        latencies.sort(Comparator.naturalOrder());
        System.out.println(String.format("Fastest - %s mcs", getMicroLatency(latencies.getFirst())));
        System.out.println(String.format("0.5 latency - %s mcs", getLatencyPercentile(latencies, 0.5)));
        System.out.println(String.format("0.9 latency - %s mcs", getLatencyPercentile(latencies, 0.9)));
        System.out.println(String.format("0.95 latency - %s mcs", getLatencyPercentile(latencies, 0.95)));
        System.out.println(String.format("0.99 latency - %s mcs", getLatencyPercentile(latencies, 0.99)));
        System.out.println(String.format("0.9999 latency - %s mcs", getLatencyPercentile(latencies, 0.9999)));
        System.out.println(String.format("1.0 latency - %s mcs", getMicroLatency(latencies.get(latencies.size() - 1))));
        System.exit(0);

    }

    private static double getLatencyPercentile(List<Long> nanoLatencies, double percentile) {
        var index = (int) (percentile * nanoLatencies.size());
        var nanoLatency = nanoLatencies.get(index);
        return getMicroLatency(nanoLatency);
    }

    private static double getMicroLatency(Long nanoLatency) {
        var microLatency = nanoLatency / 1000.0;
        return new BigDecimal(microLatency)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
