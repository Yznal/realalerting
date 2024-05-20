package ru.realalerting.metrciclient;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.producer.BaseProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientProducer extends BaseProducer {

    ClientProducer(Producer producer) {
        super(producer);
    }

    private void sendData(byte[][] tagsBytes, int requsetId, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset, Protocol.INSTRUCTION_GET_METRIC_ID);
        offset += MetricConstants.ID_SIZE;
        buf.putInt(offset, requsetId);
        offset += MetricConstants.ID_SIZE;
        buf.putInt(offset, tagsBytes.length);
        offset += MetricConstants.INT_SIZE;
        for (int i = 0; i < tagsBytes.length; ++i) {
            buf.putInt(offset, tagsBytes[i].length);
            offset += MetricConstants.INT_SIZE;
        }
        for (int i = 0; i < tagsBytes.length; i++) {
            buf.putBytes(offset, tagsBytes[i]);
            offset += tagsBytes[i].length;
        }
    }

    private byte[][] stringsToBytes(List<CharSequence> strings) {
        byte[][] result = new byte[strings.size()][];
        for (int i = 0; i < strings.size(); ++i) {
            result[i] = strings.get(i).toString().getBytes(StandardCharsets.UTF_8);
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

    public void getMetricId(int requestId, List<CharSequence> tags) {
        byte[][] tagsBytes = stringsToBytes(tags);
        int allTagsSize = allTagsSizes(tagsBytes);
        int allocatedMemory = MetricConstants.ID_SIZE + MetricConstants.ID_SIZE
                + MetricConstants.INT_SIZE + allTagsSize + tagsBytes.length * MetricConstants.INT_SIZE;
        BufferClaim curBufferClaim = bufferClaim.get();
        // int (id инструкции) + int(request id) + колиечство тэгов + сумма размеров всех тэгов + количество тэгов * int (длина тэга)
        if (producer.getPublication().tryClaim(allocatedMemory, curBufferClaim) > 0) {
            MutableDirectBuffer buf = curBufferClaim.buffer();
            sendData(tagsBytes, requestId, buf, curBufferClaim.offset());
            curBufferClaim.commit();
        } else {
            ++dataLeaked;
        }
    }
}
