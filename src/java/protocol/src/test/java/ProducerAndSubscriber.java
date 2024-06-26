import io.aeron.logbuffer.BufferClaim;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.GreaterAlert;
import ru.realalerting.subscriber.Subscriber;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.reader.ConfigReader;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Karbayev Saruar
 */
public class ProducerAndSubscriber {

    private static Producer producer;
    private static Subscriber subscriber;
    private static RealAlertingDriverContext context;

    @BeforeAll
    static void run() throws IOException {
        context = new RealAlertingDriverContext("/dev/shm/aeron");
        producer = new Producer(context, ConfigReader.readProducerFromFile("src/test/resources/ProducerConfig.yaml"));
        subscriber = new Subscriber(context, ConfigReader.readConsumerFromFile("src/test/resources/ConsumerConfig.yaml"));
    }

    @Test
    public void CheckSendReceiveInt() {
        int sendInt = 123;
        BufferClaim bufClaim = new BufferClaim();
        IdleStrategy idle = new SleepingIdleStrategy();
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }
        int index = Math.toIntExact(producer.getPublication().tryClaim(Integer.BYTES, bufClaim));
        while (index <= 0) {
            Thread.yield();
            index = Math.toIntExact(producer.getPublication().tryClaim(Integer.BYTES, bufClaim));
        }
        MutableDirectBuffer buf = bufClaim.buffer();
        int bufOffset = bufClaim.offset();
        buf.putInt(bufOffset, sendInt);
        bufClaim.commit();
        int poll = -1;
        while (poll <= 0) {
            FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                int response = buffer.getInt(offset);
                assertEquals(sendInt, response);
            };
            poll = subscriber.getSubscription().poll(handler, 256);
        }
    }

    @Test
    public void CheckSendReceiveChars() throws IOException {
        byte[] sendData = {'a', 's', 'd'};
        BufferClaim bufClaim = new BufferClaim();
        IdleStrategy idle = new SleepingIdleStrategy();
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }
        int index = Math.toIntExact(producer.getPublication().tryClaim(sendData.length, bufClaim));
        while (index <= 0) {
            Thread.yield();
            index = Math.toIntExact(producer.getPublication().tryClaim(sendData.length, bufClaim));
        }
        MutableDirectBuffer buf = bufClaim.buffer();
        int bufOffset = bufClaim.offset();
        buf.putBytes(bufOffset, sendData);
        bufClaim.commit();
        int poll = -1;
        while (poll <= 0) {
            FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                String response = buffer.getStringWithoutLengthAscii(offset, sendData.length);
                assertEquals(new String(sendData, StandardCharsets.UTF_8), response);
            };
            poll = subscriber.getSubscription().poll(handler, 256);
        }
    }

    @Test
    public void CheckSendReceiveString() throws IOException {
        String sendData = "asd";
        BufferClaim bufClaim = new BufferClaim();
        IdleStrategy idle = new SleepingIdleStrategy();
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }
        int index = Math.toIntExact(producer.getPublication().tryClaim(sendData.getBytes("UTF-16").length, bufClaim));
        while (index <= 0) {
            Thread.yield();
            index = Math.toIntExact(producer.getPublication().tryClaim(sendData.getBytes("UTF-16").length, bufClaim));
        }
        MutableDirectBuffer buf = bufClaim.buffer();
        int bufOffset = bufClaim.offset();
        buf.putStringUtf8(bufOffset, sendData);
        bufClaim.commit();
        int poll = -1;
        while (poll <= 0) {
            FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                String response = buffer.getStringUtf8(offset);
                assertEquals(sendData, response);
            };
            poll = subscriber.getSubscription().poll(handler, 256);
        }
    }

    @Test
    public void CheckSendReceiveSeveralMetric() {
        int[] id = {35, 36};
        long[] timestamp = {1234213, 123}, value = {762, 3};
        AtomicInteger messageId = new AtomicInteger(0);
        BufferClaim bufClaim = new BufferClaim();
        IdleStrategy idle = new SleepingIdleStrategy();
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }

        long index = producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, bufClaim);
        assert(index > 0);
        MutableDirectBuffer buf = bufClaim.buffer();
        buf.putInt(bufClaim.offset() + MetricConstants.ID_OFFSET, id[0]);
        buf.putLong(bufClaim.offset() + MetricConstants.TIMESTAMP_OFFSET, timestamp[0]);
        buf.putLong(bufClaim.offset() + MetricConstants.VALUE_OFFSET, value[0]);
        bufClaim.commit();

        index = producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, bufClaim);
        assert(index > 0);
        buf.putInt(bufClaim.offset() + MetricConstants.ID_OFFSET, id[1]);
        buf.putLong(bufClaim.offset() + MetricConstants.TIMESTAMP_OFFSET, timestamp[1]);
        buf.putLong(bufClaim.offset() + MetricConstants.VALUE_OFFSET, value[1]);
        bufClaim.commit();

        int poll = -1;
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            int responseId = buffer.getInt(offset);
            long responseValue = buffer.getLong(offset + MetricConstants.VALUE_OFFSET);
            long responseTimestamp = buffer.getLong(offset + MetricConstants.TIMESTAMP_OFFSET);
            assertEquals(responseId, id[messageId.get()]);
            assertEquals(responseValue, value[messageId.get()]);
            assertEquals(responseTimestamp, timestamp[messageId.getAndIncrement()]);
        };
        while (poll <= 0) {
            poll = subscriber.getSubscription().poll(handler, MetricConstants.METRIC_BYTES);
            idle.idle();
        }
    }

    @Test
    public void CheckSendReceiveBatchMetric() {
        int[] id = {35, 36};
        long[] timestamp = {1234213, 123}, value = {762, 3};
        AtomicInteger messageId = new AtomicInteger(0);
        BufferClaim bufClaim = new BufferClaim();
        IdleStrategy idle = new SleepingIdleStrategy();
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }
        while (!producer.getPublication().isConnected()) {
            idle.idle();
        }

        long index = producer.getPublication().tryClaim(2 * MetricConstants.METRIC_BYTES, bufClaim);
        assert(index > 0);
        MutableDirectBuffer buf = bufClaim.buffer();
        buf.putInt(bufClaim.offset() + MetricConstants.ID_OFFSET, id[0]);
        buf.putLong(bufClaim.offset() + MetricConstants.TIMESTAMP_OFFSET, timestamp[0]);
        buf.putLong(bufClaim.offset() + MetricConstants.VALUE_OFFSET, value[0]);

        buf.putInt(bufClaim.offset() + MetricConstants.METRIC_BYTES + MetricConstants.ID_OFFSET, id[1]);
        buf.putLong(bufClaim.offset() + MetricConstants.METRIC_BYTES + MetricConstants.TIMESTAMP_OFFSET, timestamp[1]);
        buf.putLong(bufClaim.offset() + MetricConstants.METRIC_BYTES + MetricConstants.VALUE_OFFSET, value[1]);
        bufClaim.commit();

        int poll = -1;
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            for (int i = 0; i * MetricConstants.METRIC_BYTES < length; ++i) {
                int responseId = buffer.getInt(offset + i * MetricConstants.METRIC_BYTES);
                long responseValue = buffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.VALUE_OFFSET);
                long responseTimestamp = buffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.TIMESTAMP_OFFSET);
                assertEquals(responseId, id[messageId.get()]);
                assertEquals(responseValue, value[messageId.get()]);
                assertEquals(responseTimestamp, timestamp[messageId.getAndIncrement()]);
            }
        };
        while (poll <= 0) {
            poll = subscriber.getSubscription().poll(handler, MetricConstants.METRIC_BYTES);
            idle.idle();
        }
    }

    @Test
    public void AlertProducer() throws Exception {
        IdleStrategy idle = new SleepingIdleStrategy();
        AlertProducer alertProducer = new AlertProducer(producer);
        alertProducer.waitUntilConnected();
        AlertInfo alertInfo = new AlertInfo(0, 0, 20);
        GreaterAlert greaterAlert = new GreaterAlert(alertInfo);
        alertProducer.sendAlert(greaterAlert, alertInfo.getMetricId(), 0, 12300);
        alertProducer.sendAlert(greaterAlert, alertInfo.getMetricId(), 10, 12300);
        alertProducer.sendAlert(greaterAlert, alertInfo.getMetricId(), 20, 12300);
        alertProducer.sendAlert(greaterAlert, alertInfo.getMetricId(), 30, 12300);
        alertProducer.sendAlert(greaterAlert, alertInfo.getMetricId(), 40, 12300);
        int poll = -1;
        AtomicInteger metric_count = new AtomicInteger(3);
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            for (int i = 0; i * MetricConstants.METRIC_BYTES < length; ++i) {
                int responseId = buffer.getInt(offset + i * MetricConstants.METRIC_BYTES);
                long responseValue = buffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.VALUE_OFFSET);
                long responseTimestamp = buffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.TIMESTAMP_OFFSET);
                assertEquals(responseId, 0);
                assertEquals(responseValue, 10 * metric_count.getAndIncrement());
                assertEquals(responseTimestamp, 12300);
            }
        };
        while (poll <= 0) {
            poll = subscriber.getSubscription().poll(handler, MetricConstants.METRIC_BYTES);
            idle.idle();
        }
    }
}
