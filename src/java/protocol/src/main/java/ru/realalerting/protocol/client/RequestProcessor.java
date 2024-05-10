package ru.realalerting.protocol.client;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.producer.Producer;

public interface RequestProcessor {
    void doWork(Producer producer, DirectBuffer directBuffer, int offset, int length, Header header);
}
