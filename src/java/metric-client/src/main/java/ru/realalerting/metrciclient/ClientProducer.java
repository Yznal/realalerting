package ru.realalerting.metrciclient;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;

public class ClientProducer {
    private final BufferClaim bufferClaim = new BufferClaim();
    private Producer producer;

    ClientProducer(Producer producer) {
        this.producer = producer;
    }

    private void sendData(byte[][] tagsBytes, int requsetId, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset, Protocol.INSTRUCTION_GET_METRIC_ID);
        offset += MetricConstants.LENGTH_ID;
        buf.putInt(offset, requsetId);
        offset += MetricConstants.LENGTH_ID;
        buf.putInt(offset, tagsBytes.length);
        offset += MetricConstants.INT_SIZE;
        for (int i = 0; i < tagsBytes.length; i++) {
            buf.putInt(offset, tagsBytes[i].length);
            offset += MetricConstants.INT_SIZE;
            buf.putBytes(offset, tagsBytes[i]);
            offset += tagsBytes[i].length;
        }
    }

    private byte[][] stringsToBytes(String[] strings) {
        byte[][] result = new byte[strings.length][];
        for (int i = 0; i < strings.length; ++i) {
            result[i] =strings[i].getBytes(StandardCharsets.UTF_8);
        }
        return result;
    }

    private int allTagsSizes(byte[][] tagsBytes) {
        int result = 0;
        for (int i = 0; i < tagsBytes.length; ++i) {
            result += tagsBytes[i].length;
        }
        return result;
    }

    public void getMetricId(int requestId, String[] tags) {
        byte[][] tagsBytes = stringsToBytes(tags);
        int allTagsSize = allTagsSizes(tagsBytes);
        int allocatedMemory = MetricConstants.LENGTH_ID + MetricConstants.LENGTH_ID + MetricConstants.INT_SIZE +
                allTagsSize + tagsBytes.length * MetricConstants.INT_SIZE;
        // int (id инструкции) + int(request id) + колиечство тэгов + сумма размеров всех тэгов + количество тэгов * int (длина тэга)
        if (producer.getPublication().tryClaim(allocatedMemory, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            sendData(tagsBytes, requestId, buf, bufferClaim.offset());
            bufferClaim.commit();
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(allocatedMemory, MetricConstants.LARGEST_ALIGMENT));
            sendData(tagsBytes, requestId, buf, 0);
            producer.getPublication().offer(buf);
        }
    }
}
