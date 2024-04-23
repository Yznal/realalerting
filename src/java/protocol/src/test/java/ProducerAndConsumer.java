import io.aeron.logbuffer.BufferClaim;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.AeronContext;
import ru.realalerting.protocol.ConfigReader;
import ru.realalerting.protocol.Metric;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Karbayev Saruar
 */
public class ProducerAndConsumer {

    private static Producer producer;
    private static Consumer consumer;

    @BeforeAll
    static void run() throws IOException {
        AeronContext.initialize("/dev/shm/aeron");
        producer = new Producer(AeronContext.getInstance(), ConfigReader.readProducerFromFile("src/test/resources/ProducerConfig.yaml"));
        consumer = new Consumer(AeronContext.getInstance(), ConfigReader.readConsumerFromFile("src/test/resources/ConsumerConfig.yaml"));
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
            poll = consumer.getSubscription().poll(handler, 256);
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
            poll = consumer.getSubscription().poll(handler, 256);
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
            poll = consumer.getSubscription().poll(handler, 256);
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

        long index = producer.getPublication().tryClaim(Metric.BYTES, bufClaim);
        assert(index > 0);
        MutableDirectBuffer buf = bufClaim.buffer();
        buf.putInt(bufClaim.offset() + Metric.OFFSET_ID, id[0]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp[0]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_VALUE, value[0]);
        bufClaim.commit();

        index = producer.getPublication().tryClaim(Metric.BYTES, bufClaim);
        assert(index > 0);
        buf.putInt(bufClaim.offset() + Metric.OFFSET_ID, id[1]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp[1]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_VALUE, value[1]);
        bufClaim.commit();

        int poll = -1;
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            int responseId = buffer.getInt(offset);
            long responseValue = buffer.getLong(offset + Metric.OFFSET_VALUE);
            long responseTimestamp = buffer.getLong(offset + Metric.OFFSET_TIMESTAMP);
            assertEquals(responseId, id[messageId.get()]);
            assertEquals(responseValue, value[messageId.get()]);
            assertEquals(responseTimestamp, timestamp[messageId.getAndIncrement()]);
        };
        while (poll <= 0) {
            poll = consumer.getSubscription().poll(handler, Metric.BYTES);
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

        long index = producer.getPublication().tryClaim(2 * Metric.BYTES, bufClaim);
        assert(index > 0);
        MutableDirectBuffer buf = bufClaim.buffer();
        buf.putInt(bufClaim.offset() + Metric.OFFSET_ID, id[0]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp[0]);
        buf.putLong(bufClaim.offset() + Metric.OFFSET_VALUE, value[0]);

        buf.putInt(bufClaim.offset() + Metric.BYTES + Metric.OFFSET_ID, id[1]);
        buf.putLong(bufClaim.offset() + Metric.BYTES + Metric.OFFSET_TIMESTAMP, timestamp[1]);
        buf.putLong(bufClaim.offset() + Metric.BYTES + Metric.OFFSET_VALUE, value[1]);
        bufClaim.commit();

        int poll = -1;
        FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
            for (int i = 0; i * Metric.BYTES < length; ++i) {
                int responseId = buffer.getInt(offset + i * Metric.BYTES);
                long responseValue = buffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_VALUE);
                long responseTimestamp = buffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_TIMESTAMP);
                assertEquals(responseId, id[messageId.get()]);
                assertEquals(responseValue, value[messageId.get()]);
                assertEquals(responseTimestamp, timestamp[messageId.getAndIncrement()]);
            }
        };
        while (poll <= 0) {
            poll = consumer.getSubscription().poll(handler, Metric.BYTES);
            idle.idle();
        }
    }
}
