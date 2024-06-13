package ru.realalerting.protocol.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.logbuffer.BufferClaim;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.agrona.MutableDirectBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;

public class RequestConstants {

    public static boolean sendGetMetricIdWithCriticalAlert(BufferClaim bufferClaim, int protocolInstruction,
                                                           int requestId, int metricId, String uri, int streamId,
                                                           Producer producer, RowSet<Row> rows) {
        boolean isSended = false;
        byte[] uriBytes = uri.getBytes(StandardCharsets.UTF_8);
        int allocatedMemory = MetricConstants.INT_SIZE + MetricConstants.ID_SIZE + MetricConstants.ID_SIZE +
                MetricConstants.INT_SIZE + MetricConstants.INT_SIZE + uriBytes.length + MetricConstants.INT_SIZE +
                MetricConstants.ID_SIZE * rows.size() + (MetricConstants.INT_SIZE + MetricConstants.INT_SIZE) * rows.size();
        // instructionId(int) + requestId(int) + metricId(int) + critical alert count + длина uri +
        // + количество байт в uri + streamId + alertId(int) * critical alert count +
        // + (id конфига для просчета алерта + threshold ) * critical alert count
        // TODO нужно переделать систему отправки конфиги алертов
        if (producer.getPublication().tryClaim(allocatedMemory, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            int offset = bufferClaim.offset();
            buf.putInt(offset, protocolInstruction);
            offset += MetricConstants.ID_SIZE;
            buf.putInt(offset, requestId);
            offset += MetricConstants.INT_SIZE;
            buf.putInt(offset, metricId);
            offset += MetricConstants.INT_SIZE;
            buf.putInt(offset, rows.size());
            offset += MetricConstants.INT_SIZE;
            buf.putInt(offset, uriBytes.length);
            offset += MetricConstants.INT_SIZE;
            buf.putBytes(offset, uriBytes);
            offset += uriBytes.length;
            buf.putInt(offset, streamId);
            offset += MetricConstants.INT_SIZE;
            for (Row row : rows) {
                int alertId = row.getInteger(0);
                String alertConfig = row.getString(1);
                buf.putInt(offset, alertId);
                offset += MetricConstants.INT_SIZE;
                offset = sendAlertConfig(alertConfig, producer, buf, offset);
            }
            bufferClaim.commit();
            isSended = true;
        }
        return isSended;
    }

    private static int sendAlertConfig(String alertConfig, Producer producer, MutableDirectBuffer buf, int offset) {
        try {
            JsonNode alertConfigJson = new ObjectMapper().readTree(alertConfig);
            int alertLogicConstant = alertConfigJson.get("alert logic id").asInt();
            int threshold = alertConfigJson.get("threshold").asInt();
            buf.putInt(alertLogicConstant, offset);
            offset += MetricConstants.INT_SIZE;
            buf.putInt(offset, threshold);
            offset += MetricConstants.INT_SIZE;
            return offset;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
