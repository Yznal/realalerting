package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricMetaTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTagsValueTestSamples.*;
import static ru.realalerting.controlplane.domain.TenantTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class TenantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tenant.class);
        Tenant tenant1 = getTenantSample1();
        Tenant tenant2 = new Tenant();
        assertThat(tenant1).isNotEqualTo(tenant2);

        tenant2.setId(tenant1.getId());
        assertThat(tenant1).isEqualTo(tenant2);

        tenant2 = getTenantSample2();
        assertThat(tenant1).isNotEqualTo(tenant2);
    }

    @Test
    void clientTest() throws Exception {
        Tenant tenant = getTenantRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        tenant.addClient(clientBack);
        assertThat(tenant.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getTenant()).isEqualTo(tenant);

        tenant.removeClient(clientBack);
        assertThat(tenant.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getTenant()).isNull();

        tenant.clients(new HashSet<>(Set.of(clientBack)));
        assertThat(tenant.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getTenant()).isEqualTo(tenant);

        tenant.setClients(new HashSet<>());
        assertThat(tenant.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getTenant()).isNull();
    }

    @Test
    void metricTagsValueTest() throws Exception {
        Tenant tenant = getTenantRandomSampleGenerator();
        MetricTagsValue metricTagsValueBack = getMetricTagsValueRandomSampleGenerator();

        tenant.addMetricTagsValue(metricTagsValueBack);
        assertThat(tenant.getMetricTagsValues()).containsOnly(metricTagsValueBack);
        assertThat(metricTagsValueBack.getTenant()).isEqualTo(tenant);

        tenant.removeMetricTagsValue(metricTagsValueBack);
        assertThat(tenant.getMetricTagsValues()).doesNotContain(metricTagsValueBack);
        assertThat(metricTagsValueBack.getTenant()).isNull();

        tenant.metricTagsValues(new HashSet<>(Set.of(metricTagsValueBack)));
        assertThat(tenant.getMetricTagsValues()).containsOnly(metricTagsValueBack);
        assertThat(metricTagsValueBack.getTenant()).isEqualTo(tenant);

        tenant.setMetricTagsValues(new HashSet<>());
        assertThat(tenant.getMetricTagsValues()).doesNotContain(metricTagsValueBack);
        assertThat(metricTagsValueBack.getTenant()).isNull();
    }

    @Test
    void metricMetaTest() throws Exception {
        Tenant tenant = getTenantRandomSampleGenerator();
        MetricMeta metricMetaBack = getMetricMetaRandomSampleGenerator();

        tenant.setMetricMeta(metricMetaBack);
        assertThat(tenant.getMetricMeta()).isEqualTo(metricMetaBack);
        assertThat(metricMetaBack.getTenant()).isEqualTo(tenant);

        tenant.metricMeta(null);
        assertThat(tenant.getMetricMeta()).isNull();
        assertThat(metricMetaBack.getTenant()).isNull();
    }
}
