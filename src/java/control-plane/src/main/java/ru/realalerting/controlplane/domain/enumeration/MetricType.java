package ru.realalerting.controlplane.domain.enumeration;

/**
 * The MetricType enumeration.
 */
public enum MetricType {
    INT,
    /**
     * возможно нужно не по типам, а по способу взаимодействия
     * счетчик (может идти только вврех),
     * числовой (вверх и вниз)
     * показатель (дробное число - значение чего-то)
     * и т.д.
     */
    DOUBLE,
}
