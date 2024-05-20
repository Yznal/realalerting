package ru.realalerting.protocol.client;

import io.aeron.logbuffer.BufferClaim;
import io.aeron.logbuffer.Header;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GetMetricId implements RequestProcessor {
    private final BufferClaim bufferClaim = new BufferClaim();
    private final ConcurrentHashMap<List<CharSequence>, Integer> idByTags = new ConcurrentHashMap<>();

    @Override
    public void doWork(Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {
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
        Integer metricId = idByTags.get(tags);
        if (metricId != null) {
            sendGetMetricIdResponse(producer, requestId, metricId);
        } else {
            createMetricId(tags);
        }
    }

    public void addToMap(List<CharSequence> tags, int metricId) { // TODO функцию после того как подключим ControlPlane
        idByTags.put(tags, metricId);
    }

    private void updateMap() {
        // TODO периодически должен скачивать и подгружать в мапу из ControlPlane асинхронно
    }

    private void createMetricId(List<CharSequence> tags) {

        // TODO если нет id, то должен его создать асинхронно в bd
        //  database.getMetricId(String[] tags, IntConsumer callback)
    }

    private void sendData(int requestId, int metricId, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset, Protocol.INSTRUCTION_SET_METRIC_ID);
        offset += MetricConstants.ID_SIZE;
        buf.putInt(offset, requestId);
        offset += MetricConstants.INT_SIZE;
        buf.putInt(offset, metricId);
        offset += MetricConstants.INT_SIZE;
    }

    private boolean sendGetMetricIdResponse(Producer producer, int requestId, int metricId) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            sendData(requestId, metricId, buf, bufferClaim.offset());
            bufferClaim.commit();
            isSended = true;
        }
        return isSended;
    }

}
