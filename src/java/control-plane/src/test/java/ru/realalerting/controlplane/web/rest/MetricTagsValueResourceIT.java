package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.MetricTagsValueAsserts.*;
import static ru.realalerting.controlplane.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.realalerting.controlplane.IntegrationTest;
import ru.realalerting.controlplane.domain.Metric;
import ru.realalerting.controlplane.domain.MetricTagsValue;
import ru.realalerting.controlplane.domain.Tenant;
import ru.realalerting.controlplane.repository.MetricTagsValueRepository;

/**
 * Integration tests for the {@link MetricTagsValueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MetricTagsValueResourceIT {

    private static final String DEFAULT_VALUE_1 = "AAAAAAAAAA";
    private static final String UPDATED_VALUE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE_256 = "AAAAAAAAAA";
    private static final String UPDATED_VALUE_256 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/metric-tags-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricTagsValueRepository metricTagsValueRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetricTagsValueMockMvc;

    private MetricTagsValue metricTagsValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricTagsValue createEntity(EntityManager em) {
        MetricTagsValue metricTagsValue = new MetricTagsValue().value1(DEFAULT_VALUE_1).value256(DEFAULT_VALUE_256);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        metricTagsValue.setMetric(metric);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        metricTagsValue.setTenant(tenant);
        return metricTagsValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricTagsValue createUpdatedEntity(EntityManager em) {
        MetricTagsValue metricTagsValue = new MetricTagsValue().value1(UPDATED_VALUE_1).value256(UPDATED_VALUE_256);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createUpdatedEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        metricTagsValue.setMetric(metric);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        metricTagsValue.setTenant(tenant);
        return metricTagsValue;
    }

    @BeforeEach
    public void initTest() {
        metricTagsValue = createEntity(em);
    }

    @Test
    @Transactional
    void createMetricTagsValue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MetricTagsValue
        var returnedMetricTagsValue = om.readValue(
            restMetricTagsValueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricTagsValue)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MetricTagsValue.class
        );

        // Validate the MetricTagsValue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMetricTagsValueUpdatableFieldsEquals(returnedMetricTagsValue, getPersistedMetricTagsValue(returnedMetricTagsValue));
    }

    @Test
    @Transactional
    void createMetricTagsValueWithExistingId() throws Exception {
        // Create the MetricTagsValue with an existing ID
        metricTagsValue.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetricTagsValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricTagsValue)))
            .andExpect(status().isBadRequest());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMetricTagsValues() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        // Get all the metricTagsValueList
        restMetricTagsValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metricTagsValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value1").value(hasItem(DEFAULT_VALUE_1)))
            .andExpect(jsonPath("$.[*].value256").value(hasItem(DEFAULT_VALUE_256)));
    }

    @Test
    @Transactional
    void getMetricTagsValue() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        // Get the metricTagsValue
        restMetricTagsValueMockMvc
            .perform(get(ENTITY_API_URL_ID, metricTagsValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metricTagsValue.getId().intValue()))
            .andExpect(jsonPath("$.value1").value(DEFAULT_VALUE_1))
            .andExpect(jsonPath("$.value256").value(DEFAULT_VALUE_256));
    }

    @Test
    @Transactional
    void getNonExistingMetricTagsValue() throws Exception {
        // Get the metricTagsValue
        restMetricTagsValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMetricTagsValue() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricTagsValue
        MetricTagsValue updatedMetricTagsValue = metricTagsValueRepository.findById(metricTagsValue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetricTagsValue are not directly saved in db
        em.detach(updatedMetricTagsValue);
        updatedMetricTagsValue.value1(UPDATED_VALUE_1).value256(UPDATED_VALUE_256);

        restMetricTagsValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMetricTagsValue.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMetricTagsValue))
            )
            .andExpect(status().isOk());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMetricTagsValueToMatchAllProperties(updatedMetricTagsValue);
    }

    @Test
    @Transactional
    void putNonExistingMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, metricTagsValue.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metricTagsValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metricTagsValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricTagsValue)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMetricTagsValueWithPatch() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricTagsValue using partial update
        MetricTagsValue partialUpdatedMetricTagsValue = new MetricTagsValue();
        partialUpdatedMetricTagsValue.setId(metricTagsValue.getId());

        partialUpdatedMetricTagsValue.value1(UPDATED_VALUE_1).value256(UPDATED_VALUE_256);

        restMetricTagsValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricTagsValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricTagsValue))
            )
            .andExpect(status().isOk());

        // Validate the MetricTagsValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricTagsValueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMetricTagsValue, metricTagsValue),
            getPersistedMetricTagsValue(metricTagsValue)
        );
    }

    @Test
    @Transactional
    void fullUpdateMetricTagsValueWithPatch() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricTagsValue using partial update
        MetricTagsValue partialUpdatedMetricTagsValue = new MetricTagsValue();
        partialUpdatedMetricTagsValue.setId(metricTagsValue.getId());

        partialUpdatedMetricTagsValue.value1(UPDATED_VALUE_1).value256(UPDATED_VALUE_256);

        restMetricTagsValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricTagsValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricTagsValue))
            )
            .andExpect(status().isOk());

        // Validate the MetricTagsValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricTagsValueUpdatableFieldsEquals(
            partialUpdatedMetricTagsValue,
            getPersistedMetricTagsValue(partialUpdatedMetricTagsValue)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, metricTagsValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricTagsValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricTagsValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMetricTagsValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricTagsValue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricTagsValueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(metricTagsValue)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricTagsValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetricTagsValue() throws Exception {
        // Initialize the database
        metricTagsValueRepository.saveAndFlush(metricTagsValue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the metricTagsValue
        restMetricTagsValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, metricTagsValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return metricTagsValueRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected MetricTagsValue getPersistedMetricTagsValue(MetricTagsValue metricTagsValue) {
        return metricTagsValueRepository.findById(metricTagsValue.getId()).orElseThrow();
    }

    protected void assertPersistedMetricTagsValueToMatchAllProperties(MetricTagsValue expectedMetricTagsValue) {
        assertMetricTagsValueAllPropertiesEquals(expectedMetricTagsValue, getPersistedMetricTagsValue(expectedMetricTagsValue));
    }

    protected void assertPersistedMetricTagsValueToMatchUpdatableProperties(MetricTagsValue expectedMetricTagsValue) {
        assertMetricTagsValueAllUpdatablePropertiesEquals(expectedMetricTagsValue, getPersistedMetricTagsValue(expectedMetricTagsValue));
    }
}
