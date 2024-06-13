package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AlertSubscriberAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlertSubscriberAllPropertiesEquals(AlertSubscriber expected, AlertSubscriber actual) {
        assertAlertSubscriberAutoGeneratedPropertiesEquals(expected, actual);
        assertAlertSubscriberAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlertSubscriberAllUpdatablePropertiesEquals(AlertSubscriber expected, AlertSubscriber actual) {
        assertAlertSubscriberUpdatableFieldsEquals(expected, actual);
        assertAlertSubscriberUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlertSubscriberAutoGeneratedPropertiesEquals(AlertSubscriber expected, AlertSubscriber actual) {
        assertThat(expected)
            .as("Verify AlertSubscriber auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlertSubscriberUpdatableFieldsEquals(AlertSubscriber expected, AlertSubscriber actual) {
        assertThat(expected)
            .as("Verify AlertSubscriber relevant properties")
            .satisfies(e -> assertThat(e.getSubscriberAddress()).as("check subscriberAddress").isEqualTo(actual.getSubscriberAddress()))
            .satisfies(e -> assertThat(e.getSubscriberPort()).as("check subscriberPort").isEqualTo(actual.getSubscriberPort()))
            .satisfies(e -> assertThat(e.getSubscriberUri()).as("check subscriberUri").isEqualTo(actual.getSubscriberUri()))
            .satisfies(e -> assertThat(e.getSubscriberStreamId()).as("check subscriberStreamId").isEqualTo(actual.getSubscriberStreamId()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlertSubscriberUpdatableRelationshipsEquals(AlertSubscriber expected, AlertSubscriber actual) {
        assertThat(expected)
            .as("Verify AlertSubscriber relationships")
            .satisfies(e -> assertThat(e.getClient()).as("check client").isEqualTo(actual.getClient()))
            .satisfies(e -> assertThat(e.getRealAlert()).as("check realAlert").isEqualTo(actual.getRealAlert()));
    }
}