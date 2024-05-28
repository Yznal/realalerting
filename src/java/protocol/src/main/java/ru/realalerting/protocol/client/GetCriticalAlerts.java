package ru.realalerting.protocol.client;

import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.SqlClient;
import org.agrona.DirectBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;

public class GetCriticalAlerts implements RequestProcessor {

    @Override
    public void doWork(int clientId, SqlClient client, Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {
        // TODO отправляем запрос на базу, чтобы получить все алерты для этой метрикиы
        int requestId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int metricId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;




    }
}
