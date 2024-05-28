package ru.realalerting.protocol.client;

import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import ru.realalerting.producer.Producer;

public interface RequestProcessor {
    void doWork(int clientId, SqlClient database, Producer producer, DirectBuffer directBuffer, int offset, int length, Header header);
}

// TODO в схеме jdl нужно еще Alert подключить к клиенту
