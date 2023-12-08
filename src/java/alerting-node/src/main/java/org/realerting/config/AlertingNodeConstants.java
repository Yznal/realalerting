package org.realerting.config;

import static org.agrona.BitUtil.*;

public class AlertingNodeConstants {
    /*
     * Длины атрибутов метрик
     */
    public static final int METRIC_ID_LENGTH = SIZE_OF_INT;
    public static final int METRIC_VALUE_LENGTH = SIZE_OF_DOUBLE;
    public static final int METRIC_TIMESTAMP_LENGTH = SIZE_OF_LONG;
    public static final int MESSAGE_LENGTH = METRIC_ID_LENGTH + METRIC_VALUE_LENGTH + METRIC_TIMESTAMP_LENGTH;
    /*
     * Офсеты атрибутов метрик
     */
    public static final int METRIC_ID_OFFSET = 0;
    public static final int METRIC_VALUE_OFFSET = METRIC_ID_OFFSET + METRIC_ID_LENGTH;
    public static final int METRIC_TIMESTAMP_OFFSET = METRIC_VALUE_OFFSET + METRIC_VALUE_LENGTH;
    /*
     * Прочие константы
     */
    public static final int ALIGNMENT = 4;
    public static final int ATTEMPTS_TO_RESEND = 3;
    public static final String AERON_ENDPOINT_FORMAT = "aeron:udp?endpoint=%s:%s";
}
