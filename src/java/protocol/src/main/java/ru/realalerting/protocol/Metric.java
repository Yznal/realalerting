package ru.realalerting.protocol;

import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;

/**
 * @author Karbayev Saruar
 */
public final class Metric {
    public static final int LENGTH_ID = SIZE_OF_INT;
    public static final int LENGTH_VALUE = SIZE_OF_LONG;
    public static final int LENGTH_TIMESTAMP = SIZE_OF_LONG;
    public static final int BYTES = LENGTH_ID + LENGTH_TIMESTAMP + LENGTH_VALUE;


    public static final int OFFSET_ID = 0;
    public static final int OFFSET_VALUE = OFFSET_ID + LENGTH_ID;
    public static final int OFFSET_TIMESTAMP = OFFSET_VALUE + LENGTH_VALUE;

    public static final int ALIGNMENT = 32;


    private Metric() {};

    public static void write(MutableDirectBuffer buffer, int id, long timestamp, long value) {

    }

    public static void write(MutableDirectBuffer buffer, int id, long timestamp, double value) {

    }
}
