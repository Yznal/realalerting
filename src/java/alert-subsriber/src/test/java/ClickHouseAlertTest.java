import org.agrona.concurrent.SleepingIdleStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertsubscriber.clickhouse.ClickHouseHttpClientBuilder;
import ru.realalerting.alertsubscriber.clickhouse.ClickHouseProperties;
import ru.realalerting.alertsubscriber.clickhouse.ClickHouseSender;
import ru.realalerting.alertsubscriber.clickhouse.ClickhouseJdbcUrlParser;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.reader.RealAlertingConfig;

import java.io.IOException;
import org.apache.http.client.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class ClickHouseAlertTest {
    private static ClickHouseSender sender;
    private static AlertProducer producer;
    private static int alertId = 0;

//    @BeforeAll
//    static void setUp() throws Exception {
//        RealAlertingDriverContext context = new RealAlertingDriverContext("/dev/shm/aeron");
//        RealAlertingConfig config = ConfigReader.readConsumerFromFile("src/test/resources/ConsumerConfig.yaml");
//        String url = "jdbc:clickhouse://localhost:8123";
//        ClickHouseProperties properties = ClickhouseJdbcUrlParser.parse(url, new ClickHouseProperties().asProperties());
//        HttpClient httpClient = new ClickHouseHttpClientBuilder(properties).buildClient();
//
//        sender = new ClickHouseSender(context, config, new SleepingIdleStrategy(), alertId, url, httpClient, properties);
//        producer = new AlertProducer(new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ProducerConfig.yaml")));
//    }

//    @Test
//    public void test() {
//        producer.waitUntilConnected();
//        int threshold = 20;
//        AlertInfo info = new AlertInfo(alertId, 0, threshold);
//        GreaterAlert greaterAlert = new GreaterAlert(info);
//        for (int i = 0; i < 500; ++i) {
//            boolean isSended = producer.sendAlert(greaterAlert, info.getMetricId(), i, 12300 + i);
//            if (i <= threshold) {
//                assertFalse(isSended);
//            } else {
//                assertTrue(isSended);
//            }
//        }
//        sender.run(); // TODO запустить в отдельном потоке и перенести до waitUntilConnected
//    }

}
