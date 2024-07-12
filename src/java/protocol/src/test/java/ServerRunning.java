import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import java.io.IOException;

public class ServerRunning {

    private static RealAlertingDriverContext context;

    // Protocol data
    private static final int clientId = 1;
    private static ApiNode apiNode;
    private static SqlClient client;

    // Alert System data
    private static AlertNode alertNode1;
//    private static AlertNode alertNode2;

    private static int alertCount = 3;


    private static void setupApiNode() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");

        // вставить нужное подключение
        PgConnectOptions connectOptions = new PgConnectOptions();

        PoolOptions poolOptions = new PoolOptions().setMaxSize(10);
        client = PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .build();
        apiNode = new ApiNode(client);
        Producer protocolServerProducer = new Producer(context, ConfigReader.readProducerFromFile("/home/random/protocol (saruar)/src/test/resources/ServerRunning/ServerProducerConfig.yaml"));
        Subscriber protocolServerSubscriber = new Subscriber(context, ConfigReader.readSubscriberFromFile("/home/random/protocol (saruar)/src/test/resources/ServerRunning/ServerSubscriberConfig.yaml"));

        apiNode.addClient(clientId, protocolServerProducer, protocolServerSubscriber);
        apiNode.startClient(clientId);
    }

    private static void setUpAlertNodes() throws Exception {
        Subscriber alertNodeSubscriber1 = new Subscriber(context, ConfigReader.readSubscriberFromFile("/home/random/protocol (saruar)/src/test/resources/ServerRunning/AlertNodeSubscriber1Config.yaml"));
//        Subscriber alertNodeSubsriber2 = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/Running/AlertNodeConsumer2Config.yaml"));

        alertNode1 = new AlertNode(alertNodeSubscriber1);
//        alertNode2 = new AlertNode(alertNodeProducer2, alertNodeSubsriber2);

        for (int i = 1; i <= alertCount; ++i) {
            AlertInfo alertInfo = new AlertInfo(100 + i, 100 + i, 0);
            AlertLogicBase alertLogicBase = new GreaterAlert(alertInfo);
            Producer alertProducer1 = new Producer(context, ConfigReader.readProducerFromFile("/home/random/protocol (saruar)/src/test/resources/ServerRunning/AlertProducer/AlertProducerConfig" + i + "_1" + ".yaml"));
//            Producer alertProducer2 = new Producer(context, ConfigReader.readProducerFromFile("/home/random/protocol (saruar)/src/test/resources/ServerRunning/AlertProducer/AlertProducerConfig" + i + "_2" + ".yaml"));
            alertNode1.addAlert(alertInfo, alertLogicBase);
            alertNode1.addAlertProducer(100 + i, alertProducer1);
//            alertNode2.addAlert(alertInfo, alertLogicBase);
//            alertNode2.addAlertProducer(100 + i, alertProducer2);
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
