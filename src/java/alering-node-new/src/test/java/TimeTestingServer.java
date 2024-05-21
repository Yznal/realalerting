import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.alertlogic.*;
import ru.realalerting.alertnode.AlertNode;
import ru.realalerting.alertnode.AlertSystemBalancer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.protocol.client.GetMetricId;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.subscriber.Subscriber;

public class TimeTestingServer {

    int clientCount = 3;
    int alertPerClient = 4;
    int threshold = 100;

    private class ApiNode {
        Producer serverProducer;
        Subscriber serverSubscriber;
    }

    public void main() throws Exception {
        RealAlertingDriverContext context = new RealAlertingDriverContext("/dev/shm/aeron");
        ApiNode[] apiNodes = new ApiNode[clientCount];

        AlertSystemBalancer alertSystemBalancer = new AlertSystemBalancer();
        AlertNode[] alertNodes = new AlertNode[2 * clientCount];

        AlertInfo[] alertInfos = new AlertInfo[alertPerClient];
        AlertLogicBase[] alertLogicBases = new AlertLogicBase[alertPerClient];
        for (int i = 0; i < alertPerClient; ++i) {
            alertInfos[i] = new AlertInfo(i, alertPerClient - i, threshold);
            alertLogicBases[i] = switch (i % 5) {
                case 0 -> new GreaterAlert(alertInfos[i]);
                case 1 -> new LessAlert(alertInfos[i]);
                case 2 -> new EqualAlert(alertInfos[i]);
                case 3 -> new GreaterOrEqualAlert(alertInfos[i]);
                case 4 -> new LessOrEqualAlert(alertInfos[i]);
                default -> throw new IllegalStateException("Unexpected value: " + i % 5);
            };
        }

        for (int i = 0; i < clientCount; ++i) {
            ApiNode node = new ApiNode();
            node.serverProducer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ServerProducerConfig" + i + ".yaml"));
            node.serverSubscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ServerSubscriberConfig" + i + ".yaml"));
            apiNodes[i] = node;


            alertSystemBalancer.clientCreated(i);
            Producer alertNodeProducer1 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeProducerConfig" + i + "_1.yaml"));
            Producer alertNodeProducer2 = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeProducerConfig" + i + "_2.yaml"));
            Subscriber alertNodeSubscriber1 = new Subscriber(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeSubscriberConfig" + i + "_1.yaml"));
            Subscriber alertNodeSubscriber2 = new Subscriber(context, ConfigReader.readProducerFromFile("src/test/resources/AlertNodeSubscriberConfig" + i + "_2.yaml"));
            alertSystemBalancer.assignClientToAlertNode(i, alertNodeProducer1, alertNodeProducer2, alertNodeSubscriber1, alertNodeSubscriber2);
            AlertNode[] curClientAlertNodes = alertSystemBalancer.getAlertNodesByClientId(i);
            alertNodes[2 * i] = curClientAlertNodes[0];
            alertNodes[2 * i + 1] = curClientAlertNodes[1];
            for (int j = 0; j < alertPerClient; ++j) {
                alertSystemBalancer.addAlertToClientsAlertNodes(i, alertInfos[j], alertLogicBases[j]);
            }
            alertSystemBalancer.startClientsAlertNodes(i);
            // TODO wait all producers

            GetMetricId work = new GetMetricId();
            // TODO client должен сделать work.addToMap(tagsList, metricId)
            int finalI = i;
            FragmentHandler serverGetIdHandler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                buffer.getInt(offset); // вытаскиваем id инструкции
                offset += MetricConstants.INT_SIZE;
                work.doWork(apiNodes[finalI].serverProducer, buffer, offset, length, header);
            };
            new Thread(() -> {
                int poll = -1;
                while (poll <= 0) {
                    poll = apiNodes[finalI].serverSubscriber.getSubscription().poll(serverGetIdHandler, 1000);
                    apiNodes[finalI].serverSubscriber.getIdle().idle();
                }
            }).start();
        }

    }
}
