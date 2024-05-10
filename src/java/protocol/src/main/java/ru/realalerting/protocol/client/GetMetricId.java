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

public class GetMetricId implements RequestProcessor {
    private final BufferClaim bufferClaim = new BufferClaim();

    @Override
    public void doWork(Producer producer, DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int tagsCount = directBuffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        String[] tags = new String[tagsCount];
        byte[][] tagsBytes = new byte[tagsCount][];
        // TODO нужен один массив (возможно лучше засунуть сначала все размеры)
        //  а потом byte отправлять
        int tagLen;
        for (int i = 0; i < tagsCount; i++) {
            tagLen = directBuffer.getInt(offset);
            offset += MetricConstants.INT_SIZE;
            tagsBytes[i] = new byte[tagLen];
            directBuffer.getBytes(offset, tagsBytes[i], 0, tagLen);
            offset += tagLen;
            tags[i] = new String(tagsBytes[i], StandardCharsets.UTF_8); // TODO конструктор с offset
            // TODO вместо String charSequence при передаче
        }
        int metricId = getMetricId(tags);
        // TODO если нет id, то должен его создать ассинхроно в bd
        sendGetMetricIdResponse(producer, requestId, metricId);
    }

    private int getMetricId(String[] tags) {
        return 0;
        // TODO запрос в ControlPlane
        //  должен быть ассинхронным database.getMetricId(String[] tags, IntConsumer callback)
        //  создать map где храняться id метрик, но тогда нужно периодически подгружать эту map из базы
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
        if (producer.getPublication().tryClaim(MetricConstants.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            sendData(requestId, metricId, buf, bufferClaim.offset());
            bufferClaim.commit();
            isSended = true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MetricConstants.BYTES, MetricConstants.ALIGNMENT));
            sendData(requestId, metricId, buf, 0);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }

}
