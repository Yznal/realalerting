package org.realerting.config;

import static org.agrona.BitUtil.SIZE_OF_DOUBLE;
import static org.agrona.BitUtil.SIZE_OF_INT;

public class AlertingNodeConstants {
    public static final int METRIC_ID_LENGTH = SIZE_OF_INT;
    public static final int METRIC_VALUE_LENGTH = SIZE_OF_DOUBLE;
    public static final int MESSAGE_LENGTH = METRIC_ID_LENGTH + METRIC_VALUE_LENGTH;
    public static final int ALIGNMENT = 4;
    public static final int ATTEMPTS_TO_RESEND = 3;
    public static final String AERON_ENDPOINT_FORMAT = "aeron:udp?endpoint=%s:%s";
}
