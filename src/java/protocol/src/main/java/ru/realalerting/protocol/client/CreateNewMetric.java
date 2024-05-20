package ru.realalerting.protocol.client;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.producer.Producer;


/**
 * @author Karbayev Saruar
 */
public class CreateNewMetric implements RequestProcessor {

    @Override
    public void doWork(Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {

    }
}
