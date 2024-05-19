package ru.realalerting.protocol;

import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;
import static org.agrona.BitUtil.SIZE_OF_DOUBLE;

/**
 * @author Karbayev Saruar
 */
public final class MetricConstants {
    public static final int ID_SIZE = SIZE_OF_INT;
    public static final int VALUE_SIZE = SIZE_OF_LONG;
    public static final int TIMESTAMPS_SIZE = SIZE_OF_LONG;
    public static final int METRIC_BYTES = ID_SIZE + TIMESTAMPS_SIZE + VALUE_SIZE;


    public static final int ID_OFFSET = 0;
    public static final int VALUE_OFFSET = ID_OFFSET + ID_SIZE;
    public static final int TIMESTAMP_OFFSET = VALUE_OFFSET + VALUE_SIZE;

    public static final int ALIGNMENT = 32;
    public static final int LARGEST_ALIGMENT = 8192;

    public static final int INT_SIZE = SIZE_OF_INT;
    public static final int LONG_SIZE = SIZE_OF_LONG;
    public static final int DOUBLE_SIZE = SIZE_OF_DOUBLE;


    private MetricConstants() {};

    public static void write(MutableDirectBuffer buffer, int id, long timestamp, long value) {

    }

    public static void write(MutableDirectBuffer buffer, int id, long timestamp, double value) {

    }
}
