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
    public static final int LENGTH_VALUE = SIZE_OF_LONG; // TODO !Length Size
    public static final int LENGTH_TIMESTAMP = SIZE_OF_LONG;
    public static final int BYTES = ID_SIZE + LENGTH_TIMESTAMP + LENGTH_VALUE;


    public static final int OFFSET_ID = 0;
    public static final int OFFSET_VALUE = OFFSET_ID + ID_SIZE;
    public static final int OFFSET_TIMESTAMP = OFFSET_VALUE + LENGTH_VALUE;

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
