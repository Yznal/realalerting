package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.MetricMetaTestSamples.*;
import static ru.realalerting.controlplane.domain.TenantTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class MetricMetaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricMeta.class);
        MetricMeta metricMeta1 = getMetricMetaSample1();
        MetricMeta metricMeta2 = new MetricMeta();
        assertThat(metricMeta1).isNotEqualTo(metricMeta2);

        metricMeta2.setId(metricMeta1.getId());
        assertThat(metricMeta1).isEqualTo(metricMeta2);

        metricMeta2 = getMetricMetaSample2();
        assertThat(metricMeta1).isNotEqualTo(metricMeta2);
    }

    @Test
    void tenantTest() throws Exception {
        MetricMeta metricMeta = getMetricMetaRandomSampleGenerator();
        Tenant tenantBack = getTenantRandomSampleGenerator();

        metricMeta.setTenant(tenantBack);
        assertThat(metricMeta.getTenant()).isEqualTo(tenantBack);

        metricMeta.tenant(null);
        assertThat(metricMeta.getTenant()).isNull();
    }
}
