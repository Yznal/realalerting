package ru.realalerting.metrciclient;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.Metric;
import ru.realalerting.protocol.Protocol;

import java.nio.charset.StandardCharsets;

public class ClientProducer {
    private final BufferClaim bufferClaim = new BufferClaim();
    private Producer producer;

    ClientProducer(Producer producer) {
        this.producer = producer;
    }

    public void getMetricId(String[] tags) {
        // TODO Заменить Metric.Bytes
        if (producer.getPublication().tryClaim(Metric.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            int offset = bufferClaim.offset();
            buf.putInt(offset, Protocol.INSTRUCTION_GET_METRIC_ID);
            offset += Metric.LENGTH_ID;
            buf.putInt(offset, tags.length);
            offset += Metric.LENGTH_ID;
            // TODO писать длину в буфер
            for (int i = 0; i < tags.length; i++) {
                // TODO в отдельный метод
                buf.putBytes(offset, tags[i].getBytes(StandardCharsets.UTF_8));
                // TODO посмотреть нормально ли с байтами
                offset += tags[i].length();
            }
            bufferClaim.commit();
        } else {
            // TODO тоже заменить Metric.Bytes
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(Metric.BYTES, Metric.ALIGNMENT));
            // TODO тут смущает, кажется нужно вынести и очищать при переполнении (пока не надо)
            int offset = 0;
            buf.putInt(offset, Protocol.INSTRUCTION_GET_METRIC_ID);
            offset += Metric.LENGTH_ID;
            for (int i = 0; i < tags.length; i++) {
                buf.putStringAscii(offset, tags[i]);
                offset += tags[i].length();
            }
            producer.getPublication().offer(buf);
        }
    }
}
