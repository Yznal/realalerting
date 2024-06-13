package ru.realalerting.protocol;

/*
 * All instructions related to the metric 0-10
 * All instructions related to the alert 11-20
 * All instructions related to the tenant 21-30
 */


import org.agrona.DirectBuffer;

import java.nio.charset.StandardCharsets;

/**
 * @author Karbayev Saruar
 */
public final class Protocol {
    public static final int INSTRUCTION_CREATE_NEW_METRIC = 0;
    public static final int INSTRUCTION_CREATED_NEW_METRIC = 1;

    public static final int INSTRUCTION_GET_METRIC_ID_BY_TAGS = 2;
    public static final int INSTRUCTION_SET_METRIC_ID_WITHOUT_CRITICAL_ALERTS = 3;
    public static final int INSTRUCTION_SET_METRIC_ID_WITH_CRITICAL_ALERTS = 4;

    public static final int INSTRUCTION_GET_METRIC_CRITICAL_ALERTS_BY_METRIC_ID = 5;
    public static final int INSTRUCTION_SET_METRIC_CRITICAL_ALERTS_BY_METRIC_ID = 6;
    public static final int INSTRUCTION_NO_CRITICAL_ALERTS_BY_METRIC_ID = 7;

    public static final int INSTRUCTION_SEND_METRIC = 8;

    public static final int INSTRUCTION_NEW_CRITICAL_ALERT_TYPE_1 = 11;
    public static final int INSTRUCTION_NEW_CRITICAL_ALERT_TYPE_2 = 12;
    //...



//    private Protocol() {}

}
