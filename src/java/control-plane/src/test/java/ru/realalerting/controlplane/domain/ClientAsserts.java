package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertClientAllPropertiesEquals(Client expected, Client actual) {
        assertClientAutoGeneratedPropertiesEquals(expected, actual);
        assertClientAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertClientAllUpdatablePropertiesEquals(Client expected, Client actual) {
        assertClientUpdatableFieldsEquals(expected, actual);
        assertClientUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertClientAutoGeneratedPropertiesEquals(Client expected, Client actual) {
        assertThat(expected)
            .as("Verify Client auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertClientUpdatableFieldsEquals(Client expected, Client actual) {
        assertThat(expected)
            .as("Verify Client relevant properties")
            .satisfies(
                e ->
                    assertThat(e.getProtocolProducerAddress())
                        .as("check protocolProducerAddress")
                        .isEqualTo(actual.getProtocolProducerAddress())
            )
            .satisfies(
                e -> assertThat(e.getProtocolProducerPort()).as("check protocolProducerPort").isEqualTo(actual.getProtocolProducerPort())
            )
            .satisfies(
                e -> assertThat(e.getProtocolProducerUri()).as("check protocolProducerUri").isEqualTo(actual.getProtocolProducerUri())
            )
            .satisfies(
                e ->
                    assertThat(e.getProtocolProducerStreamId())
                        .as("check protocolProducerStreamId")
                        .isEqualTo(actual.getProtocolProducerStreamId())
            )
            .satisfies(
                e ->
                    assertThat(e.getProtocolSubscriberAddress())
                        .as("check protocolSubscriberAddress")
                        .isEqualTo(actual.getProtocolSubscriberAddress())
            )
            .satisfies(
                e ->
                    assertThat(e.getProtocolSubscriberPort())
                        .as("check protocolSubscriberPort")
                        .isEqualTo(actual.getProtocolSubscriberPort())
            )
            .satisfies(
                e -> assertThat(e.getProtocolSubscriberUri()).as("check protocolSubscriberUri").isEqualTo(actual.getProtocolSubscriberUri())
            )
            .satisfies(
                e ->
                    assertThat(e.getProtocolSubscriberStreamId())
                        .as("check protocolSubscriberStreamId")
                        .isEqualTo(actual.getProtocolSubscriberStreamId())
            )
            .satisfies(
                e -> assertThat(e.getMetricProducerAddress()).as("check metricProducerAddress").isEqualTo(actual.getMetricProducerAddress())
            )
            .satisfies(e -> assertThat(e.getMetricProducerPort()).as("check metricProducerPort").isEqualTo(actual.getMetricProducerPort()))
            .satisfies(e -> assertThat(e.getMetricProducerUri()).as("check metricProducerUri").isEqualTo(actual.getMetricProducerUri()))
            .satisfies(
                e ->
                    assertThat(e.getMetricProducerStreamId())
                        .as("check metricProducerStreamId")
                        .isEqualTo(actual.getMetricProducerStreamId())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertClientUpdatableRelationshipsEquals(Client expected, Client actual) {
        assertThat(expected)
            .as("Verify Client relationships")
            .satisfies(e -> assertThat(e.getTenant()).as("check tenant").isEqualTo(actual.getTenant()));
    }
}