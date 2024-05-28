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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GetMetricId implements RequestProcessor {
    private final BufferClaim bufferClaim = new BufferClaim();
    private final String getAlertsIdsQuery = """
            SELECT real_alert.id AS "alert_id", real_alert.conf AS "alert_config", 
                alert_subscriber.subscriber_uri, alert_subscriber.subscriber_stream_id FROM real_alert
            INNER JOIN alert_subscriber ON alert_subscriber.client_id = real_alert.client_id AND 
                alert_subscriber.real_alert_id = real_alert.id
            WHERE real_alert.type = 'CRITICAL' AND real_alert.client_id = $1 AND real_alert.metric_id = $2;
            """;
    private final StringBuilder getMetricIdQueryBeginning = new StringBuilder("""
            SELECT metric.id AS "metric_id", metric.critical_alert_producer_uri, metric.critical_alert_producer_stream_id FROM metric_tags_value                                  
            INNER JOIN client ON metric_tags_value.tenant_id = client.tenant_id
            INNER JOIN metric ON metric.id = metric_tags_value.metric_id AND metric.client_id = client.id
            WHERE client.id = $1
            """); //  AND metric_tags_value.value_k = $(k + 1) добавялять в зависимости от количеста тэгов
    private final String addQuerry = " AND metric_tags_value.value_%d = %s";

    @Override
    public void doWork(int clientId, SqlClient client, Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int tagsCount = directBuffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        int[] tagsLen = new int[tagsCount];
        int allTagLen = 0;
        for (int i = 0; i < tagsCount; ++i) {
            tagsLen[i] = directBuffer.getInt(offset);
            allTagLen += tagsLen[i];
            offset += MetricConstants.INT_SIZE;
        }
        byte[] tagsBytes = new byte[allTagLen];
        directBuffer.getBytes(offset, tagsBytes, 0, allTagLen);
        offset += allTagLen;
        int byteOffset = 0;
        ArrayList<CharSequence> tags = new ArrayList<>(tagsCount);
        for (int i = 0; i < tagsCount; ++i) {
            tags.add(new String(tagsBytes, byteOffset, tagsLen[i], StandardCharsets.UTF_8));
            byteOffset += tagsLen[i];
        }
        getMetricId(clientId, client, producer, requestId, tags);
    }

    private StringBuilder addAllTagsToQuerry (List<CharSequence> tags) {
        StringBuilder finishedQuerry = new StringBuilder(getMetricIdQueryBeginning);
        for (int i = 0; i < tags.size(); ++i) {
            finishedQuerry.append(String.format(addQuerry, i + 1, tags.get(i)));
        }
        return finishedQuerry;
    }

    private void getMetricId(int clientId, SqlClient client, Producer producer, int requestId, List<CharSequence> tags) {
        StringBuilder querry = addAllTagsToQuerry(tags);
        client.preparedQuery(querry.toString())
                .execute(Tuple.of(clientId))
                .onComplete(rowSetAsyncResult -> {
                    if (rowSetAsyncResult.succeeded()) {
                        RowSet<Row> rows = rowSetAsyncResult.result();
                        if (rows.size() == 0) {
                            createMetricId(clientId, client, requestId, tags);
                        } else {
                            Row row = rows.iterator().next();
                            int metricId = row.getInteger(0);
                            CharSequence uri = row.getString(1);
                            int streamId = row.getInteger(2);
                            getMetricCiriticalAlerts(clientId, client, producer, requestId, metricId, uri, streamId);
                        }
                    } else {
                        System.err.println("Failed to get metric id: " + rowSetAsyncResult.cause().getMessage());
                    }
                });
    }

    private void getMetricCiriticalAlerts(int clientId, SqlClient client, Producer producer, int requestId,
                                          int metricId, CharSequence uri, int streamId) {
        client.preparedQuery(getAlertsIdsQuery)
                .execute(Tuple.of(clientId, metricId))
                .onComplete(rowSetAsyncResult -> {
                    if (rowSetAsyncResult.succeeded()) {
                        RowSet<Row> rows = rowSetAsyncResult.result();
                        if (rows.size() == 0) {
                            sendGetMetricIdWithoutCriticalAlertsResponse(producer, requestId, metricId);
                        } else {
                            for (Row row : rows) {
                                int alertId = row.getInteger(0);
                                String alertConfig = row.getString(1);
                                String alertUri = row.getString(2);
                                int alertStreamId = row.getInteger(3);
                                // TODO send data с алертами
                            }
                        }
                    } else {
                        System.err.println("Failed to get metric id: " + rowSetAsyncResult.cause().getMessage());
                    }
                });
    }

    private void createMetricId(int clientId, SqlClient client, int requestId, List<CharSequence> tags) {
        // TODO если нет id, то должен его создать асинхронно в bd
        //  database.getMetricId(String[] tags, IntConsumer callback)
        //  также запрашивать алерты связанные с этой метрикой
    }

    private boolean sendGetMetricIdWithoutCriticalAlertsResponse(Producer producer, int requestId, int metricId) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            int offset = bufferClaim.offset();
            buf.putInt(offset, Protocol.INSTRUCTION_SET_METRIC_ID_WITHOUT_CRITICAL_ALERTS);
            offset += MetricConstants.ID_SIZE;
            buf.putInt(offset, requestId);
            offset += MetricConstants.INT_SIZE;
            buf.putInt(offset, metricId);
            offset += MetricConstants.INT_SIZE;
            bufferClaim.commit();
            isSended = true;
        }
        return isSended;
    }
}
