package ru.realalerting.metrciclient;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.alertlogic.AlertLogicConstructor;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.protocol.MetricConstants;

import java.util.ArrayList;

public class ProtocolProcessor {

    public int[] setMetricIdWithoutAlerts(DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        int metricId = directBuffer.getInt(offset + MetricConstants.ID_SIZE);
        return new int[]{requestId, metricId};
    }

    public Object[] setMetricIdWithAlerts(DirectBuffer directBuffer, int offset, int length, Header header) {
        int requestId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int metricId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        int alertsCount = directBuffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        int uriSize = directBuffer.getInt(offset);
        offset += MetricConstants.INT_SIZE;
        byte[] uriBytes = new byte[uriSize];
        directBuffer.getBytes(offset, uriBytes);
        offset += uriSize;
        String uri = new String(uriBytes);
        int streamId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        AlertProducer alertProducer = new AlertProducer(MetricRegistry.getInstance().getDriverContext(), uri, streamId);

        Object[] response = new Object[4];
        response[0] = requestId;
        response[1] = metricId;
        response[2] = alertProducer;

        ArrayList<AlertLogicBase> alertLogics = new ArrayList<>(alertsCount);
        for (int i = 0; i < alertsCount; i++) {
            int alertId = directBuffer.getInt(offset);
            offset += MetricConstants.ID_SIZE;
            int alertLogicConstantId = directBuffer.getInt(offset);
            offset += MetricConstants.INT_SIZE;
            int threshold = directBuffer.getInt(offset);
            offset += MetricConstants.INT_SIZE;
            AlertInfo alertInfo = new AlertInfo(alertId, metricId, threshold);
            AlertLogicBase alertLogicBase = AlertLogicConstructor.createAlertLogicBase(alertLogicConstantId, alertInfo);
            alertLogics.add(alertLogicBase);
        }
        response[3] = alertLogics;
        return response;
    }

}
