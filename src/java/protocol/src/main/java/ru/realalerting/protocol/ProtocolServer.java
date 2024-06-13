package ru.realalerting.protocol;

import io.vertx.sqlclient.SqlClient;
import org.agrona.concurrent.AgentRunner;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.alertnode.AlertSystemBalancer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.client.ApiNode;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolServer {

//    public static void main(String[] args) throws IOException, SQLException {
//        final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
//        final String AERON_IPC = "aeron:ipc";
//        final String AERON_MDC_PUBLICATION = "aeron:udp?control-mode=dynamic|control=%s:%s";
//        final String AERON_MDC_SUBSCRIPTION = "aeron:udp?endpoint=%s:%s|control=%s:%s|control-mode=dynamic";
//        try {
//            String adress = args[0];
//            int threshold = 200;
//            int clientCount = 1;
//            SqlClient client = DataBaseConnection.connect(5);
//            RealAlertingDriverContext context = new RealAlertingDriverContext("/dev/shm/aeron");
//            ApiNode[] apiNodes = new ApiNode[clientCount];
//            AlertSystemBalancer alertSystemBalancer = new AlertSystemBalancer();
//            int alertPerClient = 5;
//            AlertInfo[][] alertInfos = new AlertInfo[clientCount][alertPerClient];
//            AlertLogicBase[][] alertLogicBases = new AlertLogicBase[clientCount][alertPerClient];
//            for (int i = 0; i < clientCount; ++i) {
//                alertSystemBalancer.clientCreated(i);
//                String alertNodeProducerChannel = String.format(AERON_MDC_PUBLICATION, "localhost", 6005);
//                String alertNodeSubscriberChannel = String.format(AERON_MDC_SUBSCRIPTION, adress, 6003, "localhost", 6004);
//                Producer producer1 = new Producer(context, alertNodeProducerChannel, 2 * i + 1);
//                Producer producer2 = new Producer(context, alertNodeProducerChannel, 2 * i + 2);
//                Subscriber subscriber1 = new Subscriber(context, alertNodeSubscriberChannel, 2 * i + 1);
//                Subscriber subscriber2 = new Subscriber(context, alertNodeSubscriberChannel, 2 * i + 2);
//                alertSystemBalancer.assignClientToAlertNode(i, producer1, producer2, subscriber1, subscriber2);
//                for (int j = 0; j < alertPerClient; ++j) {
//                    alertInfos[i][j] = new AlertInfo(i, i, threshold);
//                    alertLogicBases[i][j] = new GreaterAlert(alertInfos[i][j]);
//                    alertSystemBalancer.addAlertToClientsAlertNodes(i, alertInfos[i][j], alertLogicBases[i][j]);
//                    alertSystemBalancer.startClientsAlertNodes(i);
//                }
//                String producerChannel = String.format(AERON_UDP_FORMAT, "localhost", 6002);
//                String subscriberChannel = String.format(AERON_UDP_FORMAT, "localhost", 6001);
//                Producer producer = new Producer(context, producerChannel, i + 1);
//                Subscriber subscriber = new Subscriber(context, subscriberChannel, i + 1);
//                apiNodes[i] = new ApiNode(producer, subscriber, client);
//                List<CharSequence> tags = new ArrayList<>(2);
//                tags.add("ya");
//                tags.add("south");
//                apiNodes[i].getGetMetricId().addToMap(tags, 12);
//                final AgentRunner receiveAgentRunner = new AgentRunner(apiNodes[i].getApiRequestSubscriber().getIdle(),
//                        Throwable::printStackTrace, null, apiNodes[i]);
//                AgentRunner.startOnThread(receiveAgentRunner);
//            }
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//    }
}
