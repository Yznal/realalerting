package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.AlertTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class AlertTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alert.class);
        Alert alert1 = getAlertSample1();
        Alert alert2 = new Alert();
        assertThat(alert1).isNotEqualTo(alert2);

        alert2.setId(alert1.getId());
        assertThat(alert1).isEqualTo(alert2);

        alert2 = getAlertSample2();
        assertThat(alert1).isNotEqualTo(alert2);
    }

    @Test
    void metricTest() throws Exception {
        Alert alert = getAlertRandomSampleGenerator();
        Metric metricBack = getMetricRandomSampleGenerator();

        alert.setMetric(metricBack);
        assertThat(alert.getMetric()).isEqualTo(metricBack);

        alert.metric(null);
        assertThat(alert.getMetric()).isNull();
    }
}
