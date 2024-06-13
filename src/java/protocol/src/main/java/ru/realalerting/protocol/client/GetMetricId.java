package ru.realalerting.protocol.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class GetMetricId implements RequestProcessor {
    private final BufferClaim bufferClaim = new BufferClaim();
    private final String getAlertsConfigsQuery = """
            SELECT real_alert.id AS "alert_id", real_alert.conf AS "alert_config" FROM real_alert
            WHERE real_alert.type = 'CRITICAL' AND real_alert.client_id = $1 AND real_alert.metric_id = $2;
            """; // works, получаем alert_id, alert_config
    private final StringBuilder getMetricIdQueryBeginning = new StringBuilder("""
            SELECT metric.id AS "metric_id", metric.critical_alert_producer_uri, 
                metric.critical_alert_producer_stream_id
            FROM metric_tags_value                                  
            INNER JOIN client ON metric_tags_value.tenant_id = client.tenant_id
            INNER JOIN metric ON metric.id = metric_tags_value.metric_id AND metric.client_id = client.id
            WHERE client.id = $1
            """); //  AND metric_tags_value.value_k = $(k + 1) добавялять в зависимости от количеста тэгов
            // работает, но нужно добавлять тэги, иначе отправится несколько, получаем metric_id, crit_alert_producer_uri, crit_alert_producer_stream_id
    private final StringBuilder createNewMetricQueryBeggining = new StringBuilder("""
            WITH new_metric as (
                INSERT INTO metric (id, type, name, client_id, description) VALUES ((SELECT MAX(id) + 1 FROM metric), 'INT', 'metric by tags', $1, 'metric created in code')
                RETURNING id
            )
            INSERT INTO metric_tags_value (id, metric_id, tenant_id, value_1, value_256) 
            (SELECT (SELECT MAX(id) + 1 FROM metric_tags_value), metric.id, client.tenant_id
            """); // TODO переписать (не работает)
    private final String addGetMetricIdQuery = " AND metric_tags_value.value_%d = '%s'";
    private final String addCreateMetricQuery = ", '%s'";

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
        getMetricId(requestId, clientId, client, producer, tags);
    }

    private StringBuilder addAllTagsToGetMetricQuery (StringBuilder query, List<CharSequence> tags) {
        StringBuilder finishedQuerry = new StringBuilder(query);
        for (int i = 0; i < tags.size(); ++i) {
            if (i == 1) { // TODO удалить и нормально сделать
                finishedQuerry.append(String.format(addGetMetricIdQuery, 256, tags.get(i)));
            } else {
                finishedQuerry.append(String.format(addGetMetricIdQuery, i + 1, tags.get(i)));
            }
        }
        return finishedQuerry;
    }

    private StringBuilder addAllTagsToCreateMetricQuery(StringBuilder query, List<CharSequence> tags) {
        StringBuilder finishedQuerry = new StringBuilder(query);
        for (int i = 0; i < tags.size(); ++i) {
            finishedQuerry.append(String.format(addCreateMetricQuery, tags.get(i)));
        }
        finishedQuerry.append(" FROM metric, client, new_metric WHERE client.id = $1 AND metric.id = new_metric.id) RETURNING metric_id;");
        return finishedQuerry;
    }

    private void getMetricId(int requestId, int clientId, SqlClient client, Producer producer, List<CharSequence> tags) {
        StringBuilder querry = addAllTagsToGetMetricQuery(getMetricIdQueryBeginning, tags);

        client.preparedQuery(querry.toString())
            .execute(Tuple.of(clientId))
            .onComplete(rowSetAsyncResult -> {
                if (rowSetAsyncResult.succeeded()) {
                    RowSet<Row> rows = rowSetAsyncResult.result();
                    if (rows.size() == 0) {
                        createMetricId(requestId, clientId, client, producer, tags);
                    } else {
                        Row row = rows.iterator().next();
                        int metricId = row.getInteger(0);
                        String uri = row.getString(1);
                        int streamId = row.getInteger(2);
                        getMetricCiriticalAlerts(requestId, clientId, metricId, uri, streamId, client, producer);
                    }
                } else {
                    System.err.println("Failed to get metric id: " + rowSetAsyncResult.cause().getMessage());
                }
            });
    }

    private void getMetricCiriticalAlerts(int requestId, int clientId, int metricId, String uri,
                                          int streamId, SqlClient client, Producer producer) {
        client.preparedQuery(getAlertsConfigsQuery)
            .execute(Tuple.of(clientId, metricId))
            .onComplete(rowSetAsyncResult -> {
                if (rowSetAsyncResult.succeeded()) {
                    RowSet<Row> rows = rowSetAsyncResult.result();
                    if (rows.size() == 0 || uri == null || uri.isEmpty()){
                        sendGetMetricIdWithoutCriticalAlertsResponse(requestId, metricId, producer);
                    } else {
                        RequestConstants.sendGetMetricIdWithCriticalAlert(bufferClaim,
                                Protocol.INSTRUCTION_SET_METRIC_ID_WITH_CRITICAL_ALERTS, requestId, metricId,
                                uri, streamId, producer, rows);
                    }
                } else {
                    System.err.println("Failed to get metric id: " + rowSetAsyncResult.cause().getMessage());
                }
            });
    }

    private void createMetricId(int requestId, int clientId, SqlClient client, Producer producer, List<CharSequence> tags) {
        StringBuilder createNewMetricQuery = addAllTagsToCreateMetricQuery(createNewMetricQueryBeggining, tags);
        client.preparedQuery(createNewMetricQuery.toString())
            .execute(Tuple.of(clientId))
            .onComplete(rowSetAsyncResult -> {
                if (rowSetAsyncResult.succeeded()) {
                    RowSet<Row> rows = rowSetAsyncResult.result();
                    Row row = rows.iterator().next();
                    int metricId = row.getInteger(0);
                    sendGetMetricIdWithoutCriticalAlertsResponse(requestId, metricId, producer);
                } else {
                    System.err.println("Failed to create metric id: " + rowSetAsyncResult.cause().getMessage());
                }
            });
    }

    private boolean sendGetMetricIdWithoutCriticalAlertsResponse(int requestId, int metricId, Producer producer) {
        boolean isSended = false;
        int allocatedMemory = MetricConstants.INT_SIZE + MetricConstants.ID_SIZE + MetricConstants.ID_SIZE;
        // instructionId(int) + requestId(int) + metricId(int)
        if (producer.getPublication().tryClaim(allocatedMemory, bufferClaim) > 0) {
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
