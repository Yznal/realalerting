import io.aeron.logbuffer.Header;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.alertsubscriber.AlertSubscriber;
import ru.realalerting.metrciclient.MetricRegistry;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.MetricSubscriber;
import ru.realalerting.subscriber.Subscriber;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerRunning {

    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_MDC_PUBLICATION = "aeron:udp?control-mode=dynamic|control=%s:%s";
    private static final String AERON_MDC_SUBSCRIPTION = "aeron:udp?endpoint=%s:%s|control=%s:%s|control-mode=dynamic";

    // Protocol data
    private static final int clientId = 1;
    private static ApiNode apiNode;
    private static RealAlertingDriverContext context;
    private static SqlClient client;

    // Alert System data
    private static AlertNode alertNode1;
//    private static AlertNode alertNode2;

    private static int alertCount = 3;


    private static void setupApiNode() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");

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
        Producer protocolServerProducer = new Producer(context, String.format(AERON_UDP_FORMAT, "localhost", 6000), 2); // "src/test/resources/Running/ServerProducerConfig.yaml"
        Subscriber protocolServerSubscriber = new Subscriber(context, String.format(AERON_UDP_FORMAT, "localhost", 6001), 1); // "src/test/resources/Running/ServerConsumerConfig.yaml"
        apiNode.addClient(clientId, protocolServerProducer, protocolServerSubscriber);
        apiNode.startClient(clientId);
    }

    private static void setUpAlertNodes() throws Exception {
//        Producer alertNodeProducer1 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/Running/AlertNodeProducer1Config.yaml"));
//        Producer alertNodeProducer2 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/Running/AlertNodeProducer2Config.yaml"));
        Subscriber alertNodeSubsriber1 = new Subscriber(context, String.format(AERON_MDC_SUBSCRIPTION, "localhost", 6007, "localhost", 6006), 4); // "src/test/resources/Running/AlertNodeConsumer1Config.yaml"
//        Subscriber alertNodeSubsriber2 = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/Running/AlertNodeConsumer2Config.yaml"));

        alertNode1 = new AlertNode(alertNodeSubsriber1);
//        alertNode2 = new AlertNode(alertNodeProducer2, alertNodeSubsriber2);

        for (int i = 1; i <= alertCount; ++i) {
                AlertInfo alertInfo = new AlertInfo(100 + i, 100 + i, 0);
                AlertLogicBase alertLogicBase = new GreaterAlert(alertInfo);
                Producer alertProducer = new Producer(context, String.format(AERON_MDC_PUBLICATION, "localhost", 6010), i); //"src/test/resources/Running/AlertProducer/AlertProducerConfig" + i + "_1" + ".yaml"
                alertNode1.addAlert(alertInfo, alertLogicBase);
                alertNode1.addAlertProducer(100 + i, alertProducer);
//                alertNode2.addAlert(alertInfo, alertLogicBase);
        }
        alertNode1.start();
//        alertNode2.start();
    }

    public static void setUp() throws Exception {
        setupApiNode();
        setUpAlertNodes();
    }

    public static void main(String[] args) throws Exception {
        setUp();
        System.out.println("working");
        while (true) {
            Thread.sleep(100);
        }
    }
}
