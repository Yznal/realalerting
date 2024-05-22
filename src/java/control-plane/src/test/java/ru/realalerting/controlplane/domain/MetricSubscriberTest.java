package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class MetricSubscriberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricSubscriber.class);
        MetricSubscriber metricSubscriber1 = getMetricSubscriberSample1();
        MetricSubscriber metricSubscriber2 = new MetricSubscriber();
        assertThat(metricSubscriber1).isNotEqualTo(metricSubscriber2);

        metricSubscriber2.setId(metricSubscriber1.getId());
        assertThat(metricSubscriber1).isEqualTo(metricSubscriber2);

        metricSubscriber2 = getMetricSubscriberSample2();
        assertThat(metricSubscriber1).isNotEqualTo(metricSubscriber2);
    }

    @Test
    void clientTest() throws Exception {
        MetricSubscriber metricSubscriber = getMetricSubscriberRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        metricSubscriber.setClient(clientBack);
        assertThat(metricSubscriber.getClient()).isEqualTo(clientBack);

        metricSubscriber.client(null);
        assertThat(metricSubscriber.getClient()).isNull();
    }

    @Test
    void metricTest() throws Exception {
        MetricSubscriber metricSubscriber = getMetricSubscriberRandomSampleGenerator();
        Metric metricBack = getMetricRandomSampleGenerator();

        metricSubscriber.setMetric(metricBack);
        assertThat(metricSubscriber.getMetric()).isEqualTo(metricBack);

        metricSubscriber.metric(null);
        assertThat(metricSubscriber.getMetric()).isNull();
    }
}
