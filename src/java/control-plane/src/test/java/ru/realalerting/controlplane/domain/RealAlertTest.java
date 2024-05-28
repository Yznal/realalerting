package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.AlertSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;
import static ru.realalerting.controlplane.domain.RealAlertTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class RealAlertTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RealAlert.class);
        RealAlert realAlert1 = getRealAlertSample1();
        RealAlert realAlert2 = new RealAlert();
        assertThat(realAlert1).isNotEqualTo(realAlert2);

        realAlert2.setId(realAlert1.getId());
        assertThat(realAlert1).isEqualTo(realAlert2);

        realAlert2 = getRealAlertSample2();
        assertThat(realAlert1).isNotEqualTo(realAlert2);
    }

    @Test
    void alertSubscriberTest() throws Exception {
        RealAlert realAlert = getRealAlertRandomSampleGenerator();
        AlertSubscriber alertSubscriberBack = getAlertSubscriberRandomSampleGenerator();

        realAlert.addAlertSubscriber(alertSubscriberBack);
        assertThat(realAlert.getAlertSubscribers()).containsOnly(alertSubscriberBack);
        assertThat(alertSubscriberBack.getRealAlert()).isEqualTo(realAlert);

        realAlert.removeAlertSubscriber(alertSubscriberBack);
        assertThat(realAlert.getAlertSubscribers()).doesNotContain(alertSubscriberBack);
        assertThat(alertSubscriberBack.getRealAlert()).isNull();

        realAlert.alertSubscribers(new HashSet<>(Set.of(alertSubscriberBack)));
        assertThat(realAlert.getAlertSubscribers()).containsOnly(alertSubscriberBack);
        assertThat(alertSubscriberBack.getRealAlert()).isEqualTo(realAlert);

        realAlert.setAlertSubscribers(new HashSet<>());
        assertThat(realAlert.getAlertSubscribers()).doesNotContain(alertSubscriberBack);
        assertThat(alertSubscriberBack.getRealAlert()).isNull();
    }

    @Test
    void clientTest() throws Exception {
        RealAlert realAlert = getRealAlertRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        realAlert.setClient(clientBack);
        assertThat(realAlert.getClient()).isEqualTo(clientBack);

        realAlert.client(null);
        assertThat(realAlert.getClient()).isNull();
    }

    @Test
    void metricTest() throws Exception {
        RealAlert realAlert = getRealAlertRandomSampleGenerator();
        Metric metricBack = getMetricRandomSampleGenerator();

        realAlert.setMetric(metricBack);
        assertThat(realAlert.getMetric()).isEqualTo(metricBack);

        realAlert.metric(null);
        assertThat(realAlert.getMetric()).isNull();
    }
}
