import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.alertsubscriber.AlertSubscriber;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;
import ru.realalerting.subscriber.Subscriber;

public class AlertSubscriberTest extends AlertSubscriber {
    public AlertSubscriberTest(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo, IdleStrategy idleStrategy, int alertId) {
        super(aeronContext, connectInfo, idleStrategy/*, alertId*/);
    }

    public AlertSubscriberTest(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo, int alertId) {
        super(aeronContext, connectInfo/*, alertId*/);
    }

    public AlertSubscriberTest(Subscriber subscriber, int alertId) {
        super(subscriber/*, alertId*/);
    }

    @Override
    public void onAlert(int alertId, int metricId, long value, long timestamp) {
        System.out.println(alertId + ":" + metricId + ":" + value + ":" + timestamp);
    }
}
