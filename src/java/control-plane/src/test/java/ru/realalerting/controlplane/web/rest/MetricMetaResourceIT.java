package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.MetricMetaAsserts.*;
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
import ru.realalerting.controlplane.domain.MetricMeta;
import ru.realalerting.controlplane.domain.Tenant;
import ru.realalerting.controlplane.repository.MetricMetaRepository;

/**
 * Integration tests for the {@link MetricMetaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MetricMetaResourceIT {

    private static final String DEFAULT_LABEL_1 = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_1 = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL_256 = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_256 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/metric-metas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricMetaRepository metricMetaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetricMetaMockMvc;

    private MetricMeta metricMeta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricMeta createEntity(EntityManager em) {
        MetricMeta metricMeta = new MetricMeta().label1(DEFAULT_LABEL_1).label256(DEFAULT_LABEL_256);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        metricMeta.setTenant(tenant);
        return metricMeta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricMeta createUpdatedEntity(EntityManager em) {
        MetricMeta metricMeta = new MetricMeta().label1(UPDATED_LABEL_1).label256(UPDATED_LABEL_256);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        metricMeta.setTenant(tenant);
        return metricMeta;
    }

    @BeforeEach
    public void initTest() {
        metricMeta = createEntity(em);
    }

    @Test
    @Transactional
    void createMetricMeta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MetricMeta
        var returnedMetricMeta = om.readValue(
            restMetricMetaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricMeta)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MetricMeta.class
        );

        // Validate the MetricMeta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMetricMetaUpdatableFieldsEquals(returnedMetricMeta, getPersistedMetricMeta(returnedMetricMeta));
    }

    @Test
    @Transactional
    void createMetricMetaWithExistingId() throws Exception {
        // Create the MetricMeta with an existing ID
        metricMeta.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetricMetaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricMeta)))
            .andExpect(status().isBadRequest());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMetricMetas() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        // Get all the metricMetaList
        restMetricMetaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metricMeta.getId().intValue())))
            .andExpect(jsonPath("$.[*].label1").value(hasItem(DEFAULT_LABEL_1)))
            .andExpect(jsonPath("$.[*].label256").value(hasItem(DEFAULT_LABEL_256)));
    }

    @Test
    @Transactional
    void getMetricMeta() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        // Get the metricMeta
        restMetricMetaMockMvc
            .perform(get(ENTITY_API_URL_ID, metricMeta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metricMeta.getId().intValue()))
            .andExpect(jsonPath("$.label1").value(DEFAULT_LABEL_1))
            .andExpect(jsonPath("$.label256").value(DEFAULT_LABEL_256));
    }

    @Test
    @Transactional
    void getNonExistingMetricMeta() throws Exception {
        // Get the metricMeta
        restMetricMetaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMetricMeta() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricMeta
        MetricMeta updatedMetricMeta = metricMetaRepository.findById(metricMeta.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetricMeta are not directly saved in db
        em.detach(updatedMetricMeta);
        updatedMetricMeta.label1(UPDATED_LABEL_1).label256(UPDATED_LABEL_256);

        restMetricMetaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMetricMeta.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMetricMeta))
            )
            .andExpect(status().isOk());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMetricMetaToMatchAllProperties(updatedMetricMeta);
    }

    @Test
    @Transactional
    void putNonExistingMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, metricMeta.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricMeta))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metricMeta))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricMeta)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMetricMetaWithPatch() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricMeta using partial update
        MetricMeta partialUpdatedMetricMeta = new MetricMeta();
        partialUpdatedMetricMeta.setId(metricMeta.getId());

        restMetricMetaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricMeta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricMeta))
            )
            .andExpect(status().isOk());

        // Validate the MetricMeta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricMetaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMetricMeta, metricMeta),
            getPersistedMetricMeta(metricMeta)
        );
    }

    @Test
    @Transactional
    void fullUpdateMetricMetaWithPatch() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricMeta using partial update
        MetricMeta partialUpdatedMetricMeta = new MetricMeta();
        partialUpdatedMetricMeta.setId(metricMeta.getId());

        partialUpdatedMetricMeta.label1(UPDATED_LABEL_1).label256(UPDATED_LABEL_256);

        restMetricMetaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricMeta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricMeta))
            )
            .andExpect(status().isOk());

        // Validate the MetricMeta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricMetaUpdatableFieldsEquals(partialUpdatedMetricMeta, getPersistedMetricMeta(partialUpdatedMetricMeta));
    }

    @Test
    @Transactional
    void patchNonExistingMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, metricMeta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricMeta))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricMeta))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMetricMeta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricMeta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMetaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(metricMeta)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricMeta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetricMeta() throws Exception {
        // Initialize the database
        metricMetaRepository.saveAndFlush(metricMeta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the metricMeta
        restMetricMetaMockMvc
            .perform(delete(ENTITY_API_URL_ID, metricMeta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return metricMetaRepository.count();
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

    protected MetricMeta getPersistedMetricMeta(MetricMeta metricMeta) {
        return metricMetaRepository.findById(metricMeta.getId()).orElseThrow();
    }

    protected void assertPersistedMetricMetaToMatchAllProperties(MetricMeta expectedMetricMeta) {
        assertMetricMetaAllPropertiesEquals(expectedMetricMeta, getPersistedMetricMeta(expectedMetricMeta));
    }

    protected void assertPersistedMetricMetaToMatchUpdatableProperties(MetricMeta expectedMetricMeta) {
        assertMetricMetaAllUpdatablePropertiesEquals(expectedMetricMeta, getPersistedMetricMeta(expectedMetricMeta));
    }
}
