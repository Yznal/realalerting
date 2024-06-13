package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricMetaAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMetricMetaAllPropertiesEquals(MetricMeta expected, MetricMeta actual) {
        assertMetricMetaAutoGeneratedPropertiesEquals(expected, actual);
        assertMetricMetaAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMetricMetaAllUpdatablePropertiesEquals(MetricMeta expected, MetricMeta actual) {
        assertMetricMetaUpdatableFieldsEquals(expected, actual);
        assertMetricMetaUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMetricMetaAutoGeneratedPropertiesEquals(MetricMeta expected, MetricMeta actual) {
        assertThat(expected)
            .as("Verify MetricMeta auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMetricMetaUpdatableFieldsEquals(MetricMeta expected, MetricMeta actual) {
        assertThat(expected)
            .as("Verify MetricMeta relevant properties")
            .satisfies(e -> assertThat(e.getLabel1()).as("check label1").isEqualTo(actual.getLabel1()))
            .satisfies(e -> assertThat(e.getLabel256()).as("check label256").isEqualTo(actual.getLabel256()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMetricMetaUpdatableRelationshipsEquals(MetricMeta expected, MetricMeta actual) {
        assertThat(expected)
            .as("Verify MetricMeta relationships")
            .satisfies(e -> assertThat(e.getTenant()).as("check tenant").isEqualTo(actual.getTenant()));
    }
}
