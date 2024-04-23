package ru.realalerting.protocol;

/*
 * All instructions related to the metric 0-10
 * All instructions related to the alert 11-20
 * All instructions related to the tenant 21-30
 */


/**
 * @author Karbayev Saruar
 */
public final class Protocol {

    public static final int INSTRUCTION_CREATE_NEW_METRIC = 0;
    public static final int INSTRUCTION_CREATED_NEW_METRIC = 1;
    public static final int INSTRUCTION_NEW_CRITICAL_ALERT_TYPE_1 = 11;
    public static final int INSTRUCTION_NEW_CRITICAL_ALERT_TYPE_2 = 12;
    //...



//    private Protocol() {}

}
