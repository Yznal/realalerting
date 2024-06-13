import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.AgentRunner;
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

public class ClientWithServerTests {
    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static int alertId = 0;
    private static int threshold = 200;
    private static Producer alertProducer;
    private SqlClient client = DataBaseConnection.connect(5);

    @BeforeAll
    static void run() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        alertProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/WithServer/AlertProducerConfig.yaml"));
        Producer metricProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/WithServer/MetricProducerConfig.yaml"));

        MetricProducer clientMetricProducer = new MetricProducer(metricProducer);
        Producer clientProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/WithServer/ClientProducerConfig.yaml"));
        Subscriber clientConsumer = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/WithServer/ClientConsumerConfig.yaml"));

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer, context);
        metricRegistry = MetricRegistry.getInstance();

        metricRegistry.getClientProducer().waitUntilConnected();
        metricRegistry.getMetricProducer().waitUntilConnected();
    }

    @Test
    public void clientConnectionTest() throws InterruptedException {
        int alertMetricId = 12;
        String[] tags = {"ya", "south"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));

        int metricsCount = 10000;
        ArrayList<Long> startAlert = new ArrayList<>(metricsCount);
        ArrayList<Long> endAlert = new ArrayList<>(metricsCount);
        // отправили запрос на metricId
        long startRequest = System.nanoTime();
        Metric metric = metricRegistry.getMetric(tagsList);
        metric.setAlertProducer(alertProducer);
        metric.addAlertLogic(new GreaterAlert(new AlertInfo(alertId, alertMetricId,  threshold)));
        long alertValue1 = 13, alertValue2 = 300;
        long alertTimestamp1 = 100, alertTimestamp2 = 100;
        AtomicInteger curAlert = new AtomicInteger(0);
        Subscriber subscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/WithServer/AlertConsumerConfig.yaml"));
        AlertSubscriberTest alertSubscriberTest = new AlertSubscriberTest(subscriber, -1);
        final AgentRunner receiveAgentRunner = new AgentRunner(alertSubscriberTest.getConsumer().getIdle(), Throwable::printStackTrace, null, alertSubscriberTest);
        AgentRunner.startOnThread(receiveAgentRunner);

        for (int i = 0; i < metricsCount; ++i) {
            metric.addValue(i + threshold + 1, alertTimestamp1++);
            startAlert.add(System.nanoTime());
        }
        Thread.sleep(1000);
        assertEquals(metric.getMetricId(), alertMetricId);
        long endRequest = System.nanoTime();
        // Получили ответ на запрос
        ArrayList<Long> latencies = new ArrayList<>(metricsCount);
        while (curAlert.get() < metricsCount) {
            Thread.sleep(20);
        }
        for (int i = 0; i < metricsCount; ++i) {
            latencies.add(endAlert.get(i) - startAlert.get(i));
        }
        System.out.println("Request response time: " + getMicroLatency(endRequest - startRequest) + " mcs");
        latencies.sort(Comparator.naturalOrder());
        System.out.println(String.format("Fastest - %s mcs", getMicroLatency(latencies.getFirst())));
        System.out.println(String.format("0.5 latency - %s mcs", getLatencyPercentile(latencies, 0.5)));
        System.out.println(String.format("0.9 latency - %s mcs", getLatencyPercentile(latencies, 0.9)));
        System.out.println(String.format("0.95 latency - %s mcs", getLatencyPercentile(latencies, 0.95)));
        System.out.println(String.format("0.99 latency - %s mcs", getLatencyPercentile(latencies, 0.99)));
//        System.out.println(String.format("1.00 latency - %s mcs", getLatencyPercentile(latencies, 1.0)));

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
