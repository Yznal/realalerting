package ru.realalerting.protocol.client;

import io.aeron.logbuffer.BufferClaim;
import io.aeron.logbuffer.Header;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;

public class GetCriticalAlerts implements RequestProcessor {
    private final BufferClaim bufferClaim = new BufferClaim();
    private final String getCriticalAlertProducer = """
            SELECT metric.critical_alert_producer_uri, metric.critical_alert_producer_stream_id FROM metric
            WHERE metric.client_id = $1 AND metric.id = $2;
            """;
    private final String getAlertsConfigsQuery = """
            SELECT real_alert.id AS "alert_id", real_alert.conf AS "alert_config" FROM real_alert
            WHERE real_alert.type = 'CRITICAL' AND real_alert.client_id = $1 AND real_alert.metric_id = $2;
            """;

    @Override
    public void doWork(int clientId, SqlClient client, Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int metricId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        client.preparedQuery(getAlertsConfigsQuery)
                .execute(Tuple.of(clientId, metricId))
                .onComplete(rowSetAsyncResult -> {
                    if (rowSetAsyncResult.succeeded()) {
                        RowSet<Row> rows = rowSetAsyncResult.result();
                        if (rows.size() == 0) {
                            sendNoCriticalAlerts(requestId, producer);
                        } else {
                            getMetricCriticalAlertProducer(requestId, clientId, metricId, producer, client, rows);
                        }
                    } else {
                        System.err.println("Failed to get alerts configs: " + rowSetAsyncResult.cause().getMessage());
                    }
                });
    }

    private boolean sendNoCriticalAlerts(int requestId, Producer producer) {
        boolean isSended = false;
        int allocatedMemory = MetricConstants.INT_SIZE + MetricConstants.ID_SIZE;
        if (producer.getPublication().tryClaim(allocatedMemory, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            int offset = bufferClaim.offset();
            buf.putInt(offset, Protocol.INSTRUCTION_NO_CRITICAL_ALERTS_BY_METRIC_ID);
            offset += MetricConstants.ID_SIZE;
            buf.putInt(offset, requestId);
            offset += MetricConstants.ID_SIZE;
            bufferClaim.commit();
            isSended = true;
        }
        return isSended;
    }

    private void getMetricCriticalAlertProducer (int requestId, int clientId, int metricId, Producer producer, SqlClient client, RowSet<Row> alertConfigsRows) {
        client.preparedQuery(getCriticalAlertProducer)
                .execute(Tuple.of(clientId, metricId))
                .onComplete(rowSetAsyncResult -> {
                    if (rowSetAsyncResult.succeeded()) {
                        RowSet<Row> rowsProducer = rowSetAsyncResult.result();
                        Row rowProducer = rowsProducer.iterator().next();
                        String uri = rowProducer.getString(0);
                        int streamId = rowProducer.getInteger(1);
                        RequestConstants.sendGetMetricIdWithCriticalAlert(bufferClaim,
                                Protocol.INSTRUCTION_SET_METRIC_CRITICAL_ALERTS_BY_METRIC_ID, requestId, metricId,
                                uri, streamId, producer, alertConfigsRows);
                    } else {
                        System.err.println("Failed to get critical alert producer: " + rowSetAsyncResult.cause().getMessage());
                    }
                });
    }
}
