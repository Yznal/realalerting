package ru.realalerting.metrciclient;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.protocol.MetricConstants;

public class MetricProcessor {

    public int[] setMetricId(DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        int metricId = directBuffer.getInt(offset + MetricConstants.ID_SIZE);
        return new int[]{requestId, metricId};
    }
}
