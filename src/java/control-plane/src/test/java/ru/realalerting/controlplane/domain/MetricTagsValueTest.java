package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.MetricTagsValueTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;
import static ru.realalerting.controlplane.domain.TenantTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class MetricTagsValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricTagsValue.class);
        MetricTagsValue metricTagsValue1 = getMetricTagsValueSample1();
        MetricTagsValue metricTagsValue2 = new MetricTagsValue();
        assertThat(metricTagsValue1).isNotEqualTo(metricTagsValue2);

        metricTagsValue2.setId(metricTagsValue1.getId());
        assertThat(metricTagsValue1).isEqualTo(metricTagsValue2);

        metricTagsValue2 = getMetricTagsValueSample2();
        assertThat(metricTagsValue1).isNotEqualTo(metricTagsValue2);
    }

    @Test
    void metricTest() throws Exception {
        MetricTagsValue metricTagsValue = getMetricTagsValueRandomSampleGenerator();
        Metric metricBack = getMetricRandomSampleGenerator();

        metricTagsValue.setMetric(metricBack);
        assertThat(metricTagsValue.getMetric()).isEqualTo(metricBack);

        metricTagsValue.metric(null);
        assertThat(metricTagsValue.getMetric()).isNull();
    }

    @Test
    void tenantTest() throws Exception {
        MetricTagsValue metricTagsValue = getMetricTagsValueRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        metricTagsValue.setTenant(tenantBack);
        assertThat(metricTagsValue.getTenant()).isEqualTo(tenantBack);

        metricTagsValue.tenant(null);
        assertThat(metricTagsValue.getTenant()).isNull();
    }
}
