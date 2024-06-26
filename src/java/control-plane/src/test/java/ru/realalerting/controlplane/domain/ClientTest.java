package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.AlertSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;
import static ru.realalerting.controlplane.domain.RealAlertTestSamples.*;
import static ru.realalerting.controlplane.domain.TenantTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class ClientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Client.class);
        Client client1 = getClientSample1();
        Client client2 = new Client();
        assertThat(client1).isNotEqualTo(client2);

        client2.setId(client1.getId());
        assertThat(client1).isEqualTo(client2);

        client2 = getClientSample2();
        assertThat(client1).isNotEqualTo(client2);
    }

    @Test
    void metricTest() throws Exception {
        Client client = getClientRandomSampleGenerator();
        Metric metricBack = getMetricRandomSampleGenerator();

        client.addMetric(metricBack);
        assertThat(client.getMetrics()).containsOnly(metricBack);
        assertThat(metricBack.getClient()).isEqualTo(client);

        client.removeMetric(metricBack);
        assertThat(client.getMetrics()).doesNotContain(metricBack);
        assertThat(metricBack.getClient()).isNull();

        client.metrics(new HashSet<>(Set.of(metricBack)));
        assertThat(client.getMetrics()).containsOnly(metricBack);
        assertThat(metricBack.getClient()).isEqualTo(client);

        client.setMetrics(new HashSet<>());
        assertThat(client.getMetrics()).doesNotContain(metricBack);
        assertThat(metricBack.getClient()).isNull();
    }

    @Test
    void metricSubscriberTest() throws Exception {
        Client client = getClientRandomSampleGenerator();
        MetricSubscriber metricSubscriberBack = getMetricSubscriberRandomSampleGenerator();

        client.addMetricSubscriber(metricSubscriberBack);
        assertThat(client.getMetricSubscribers()).containsOnly(metricSubscriberBack);
        assertThat(metricSubscriberBack.getClient()).isEqualTo(client);

        client.removeMetricSubscriber(metricSubscriberBack);
        assertThat(client.getMetricSubscribers()).doesNotContain(metricSubscriberBack);
        assertThat(metricSubscriberBack.getClient()).isNull();

        client.metricSubscribers(new HashSet<>(Set.of(metricSubscriberBack)));
        assertThat(client.getMetricSubscribers()).containsOnly(metricSubscriberBack);
        assertThat(metricSubscriberBack.getClient()).isEqualTo(client);

        client.setMetricSubscribers(new HashSet<>());
        assertThat(client.getMetricSubscribers()).doesNotContain(metricSubscriberBack);
        assertThat(metricSubscriberBack.getClient()).isNull();
    }

    @Test
    void realAlertTest() throws Exception {
        Client client = getClientRandomSampleGenerator();
        RealAlert realAlertBack = getRealAlertRandomSampleGenerator();

        client.addRealAlert(realAlertBack);
        assertThat(client.getRealAlerts()).containsOnly(realAlertBack);
        assertThat(realAlertBack.getClient()).isEqualTo(client);

        client.removeRealAlert(realAlertBack);
        assertThat(client.getRealAlerts()).doesNotContain(realAlertBack);
        assertThat(realAlertBack.getClient()).isNull();

        client.realAlerts(new HashSet<>(Set.of(realAlertBack)));
        assertThat(client.getRealAlerts()).containsOnly(realAlertBack);
        assertThat(realAlertBack.getClient()).isEqualTo(client);

        client.setRealAlerts(new HashSet<>());
        assertThat(client.getRealAlerts()).doesNotContain(realAlertBack);
        assertThat(realAlertBack.getClient()).isNull();
    }

    @Test
    void alertSubscriberTest() throws Exception {
        Client client = getClientRandomSampleGenerator();
        AlertSubscriber alertSubscriberBack = getAlertSubscriberRandomSampleGenerator();

        client.addAlertSubscriber(alertSubscriberBack);
        assertThat(client.getAlertSubscribers()).containsOnly(alertSubscriberBack);
        assertThat(alertSubscriberBack.getClient()).isEqualTo(client);

        client.removeAlertSubscriber(alertSubscriberBack);
        assertThat(client.getAlertSubscribers()).doesNotContain(alertSubscriberBack);
        assertThat(alertSubscriberBack.getClient()).isNull();

        client.alertSubscribers(new HashSet<>(Set.of(alertSubscriberBack)));
        assertThat(client.getAlertSubscribers()).containsOnly(alertSubscriberBack);
        assertThat(alertSubscriberBack.getClient()).isEqualTo(client);

        client.setAlertSubscribers(new HashSet<>());
        assertThat(client.getAlertSubscribers()).doesNotContain(alertSubscriberBack);
        assertThat(alertSubscriberBack.getClient()).isNull();
    }

    @Test
    void tenantTest() throws Exception {
        Client client = getClientRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        client.setTenant(tenantBack);
        assertThat(client.getTenant()).isEqualTo(tenantBack);

        client.tenant(null);
        assertThat(client.getTenant()).isNull();
    }
}
