import io.aeron.logbuffer.FragmentHandler;
import org.consumer.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.producer.Producer;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.protocol.AeronContext;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProducerAndConsumer {

    private static Producer producer;
    private static Consumer consumer;

    @BeforeAll
    static void run() throws IOException {
        AeronContext.initialize("/dev/shm/aeron");
        producer = Producer.initialize("src/test/resources/ProducerConfig.yaml");
        consumer = Consumer.initializeOne("src/test/resources/ConsumerConfig.yaml");
    }

    @Test
    public void CheckSendReceiveInt() {
        int sendInt = 123;
        producer.getBuffer().putInt(0, sendInt);
        while (!producer.getPublication().isConnected()) {
            producer.getIdle().idle();
        }
        long check = producer.getPublication().offer(producer.getBuffer());
        assert(check > 0);
        int poll = -1;
        while (poll <= 0) {
            FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                int response = buffer.getInt(offset);
                assertEquals(sendInt, response);
            };
            poll = consumer.getSubsription().poll(handler, 256);
        }
    }

    @Test
    public void CheckSendReceiveString() throws IOException {
        String sendData = "asd";
        producer.getBuffer().putStringUtf8(0, sendData);
        while (!producer.getPublication().isConnected()) {
            producer.getIdle().idle();
        }
        long check = producer.getPublication().offer(producer.getBuffer());
        assert(check > 0);
        int poll = -1;
        while (poll <= 0) {
            FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                String response = buffer.getStringUtf8(offset);
                assertEquals(sendData, response);
            };
            poll = consumer.getSubsription().poll(handler, 256);
        }
    }
}
