import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.metrciclient.Metric;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.ClientProtocolConnection;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// если пересоздали ControlPlane то нужно скопироать команды из Protocol/SQL/SQL For Creating.txt
public class ProtocolTest {

    private static final int clientId = 1;
    private static ApiNode apiNode;
    private static MetricRegistry metricRegistry;
    private static RealAlertingDriverContext context;
    private static SqlClient client;

    @BeforeAll
    public static void setUp() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        // Server setup
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("ControlPlane")
                .setUser("ControlPlane")
                .setPassword("fdsavcxz");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(10);
        client = PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .build();
        apiNode = new ApiNode(client);
        Producer protocolServerProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/Protocol/ServerProducerConfig.yaml"));
        Subscriber protocolServerSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/Protocol/ServerConsumerConfig.yaml"));
        apiNode.addClient(clientId, protocolServerProducer, protocolServerSubscriber);

        // Client setup
        Producer metricProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/Protocol/MetricProducerConfig.yaml"));

        MetricProducer clientMetricProducer = new MetricProducer(metricProducer);
        Producer clientProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/Protocol/ClientProducerConfig.yaml"));
        Subscriber clientConsumer = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/Protocol/ClientConsumerConfig.yaml"));

        MetricRegistry.initialize(clientProducer, clientConsumer, clientMetricProducer, context);
        metricRegistry = MetricRegistry.getInstance();

        metricRegistry.getClientProducer().waitUntilConnected();

        apiNode.startClient(clientId);
    }

    @Test
    public void testGetMetricIdWithCriticalAlerts() throws InterruptedException {
        int metricId = 11;
        String[] tags = {"ya", "west"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);

        while(metric.getMetricId() == -1) {
            Thread.sleep(50);
        }
        assertEquals(metric.getMetricId(), metricId);
    }

    @Test
    public void testGetMetricIdWithoutCriticalAlerts() throws InterruptedException {
        int metricId = 12;
        String[] tags = {"ya", "west2"};
        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
        Metric metric = metricRegistry.getMetric(tagsList);

        while(metric.getMetricId() == -1) {
            Thread.sleep(50);
        }
        assertEquals(metric.getMetricId(), metricId);
    }

    @Test
    public void testMetricIdWithCriticalAlerts() throws InterruptedException {
        int metricId = 11;
        Metric metric = metricRegistry.getMetric(metricId);

        while(metric.getAlertProducer() == null) {
            Thread.sleep(50);
        }
        assertEquals(metric.getMetricId(), metricId);
    }

    @Test
    public void testMetricIdWithoutCriticalAlerts() throws InterruptedException {
        int metricId = 12;
        Metric metric = metricRegistry.getMetric(metricId);

        Thread.sleep(1000);
        assertTrue(metric.getAlertProducer() == null);
    }

//    @Test
//    public void testCreateMetricByTags() throws InterruptedException {
//        // TODO delete metric with id = 13 by sql
//        int metricId = 13;
//        String[] tags = {"ya", "west3"};
//        List<CharSequence> tagsList = new ArrayList<CharSequence>(Arrays.asList(tags));
//        Metric metric = metricRegistry.getMetric(tagsList);
//        metric.setAlertProducer(alertProducer);
//
//        while(metric.getMetricId() == -1) {
//            Thread.sleep(50);
//        }
//        assertEquals(metric.getMetricId(), metricId);
//    }

}
