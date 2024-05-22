package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.MetricAsserts.*;
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
import ru.realalerting.controlplane.domain.Client;
import ru.realalerting.controlplane.domain.Metric;
import ru.realalerting.controlplane.domain.enumeration.MetricType;
import ru.realalerting.controlplane.repository.MetricRepository;

/**
 * Integration tests for the {@link MetricResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MetricResourceIT {

    private static final MetricType DEFAULT_TYPE = MetricType.INT;
    private static final MetricType UPDATED_TYPE = MetricType.DOUBLE;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/metrics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetricMockMvc;

    private Metric metric;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Metric createEntity(EntityManager em) {
        Metric metric = new Metric().type(DEFAULT_TYPE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        metric.setClient(client);
        return metric;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Metric createUpdatedEntity(EntityManager em) {
        Metric metric = new Metric().type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        metric.setClient(client);
        return metric;
    }

    @BeforeEach
    public void initTest() {
        metric = createEntity(em);
    }

    @Test
    @Transactional
    void createMetric() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Metric
        var returnedMetric = om.readValue(
            restMetricMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metric)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Metric.class
        );

        // Validate the Metric in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMetricUpdatableFieldsEquals(returnedMetric, getPersistedMetric(returnedMetric));
    }

    @Test
    @Transactional
    void createMetricWithExistingId() throws Exception {
        // Create the Metric with an existing ID
        metric.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetricMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metric)))
            .andExpect(status().isBadRequest());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        metric.setType(null);

        // Create the Metric, which fails.

        restMetricMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metric)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMetrics() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        // Get all the metricList
        restMetricMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metric.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getMetric() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        // Get the metric
        restMetricMockMvc
            .perform(get(ENTITY_API_URL_ID, metric.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metric.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingMetric() throws Exception {
        // Get the metric
        restMetricMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMetric() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metric
        Metric updatedMetric = metricRepository.findById(metric.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetric are not directly saved in db
        em.detach(updatedMetric);
        updatedMetric.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMetricMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMetric.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMetric))
            )
            .andExpect(status().isOk());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMetricToMatchAllProperties(updatedMetric);
    }

    @Test
    @Transactional
    void putNonExistingMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(put(ENTITY_API_URL_ID, metric.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metric)))
            .andExpect(status().isBadRequest());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metric))
            )
            .andExpect(status().isBadRequest());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metric)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMetricWithPatch() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metric using partial update
        Metric partialUpdatedMetric = new Metric();
        partialUpdatedMetric.setId(metric.getId());

        partialUpdatedMetric.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetric.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetric))
            )
            .andExpect(status().isOk());

        // Validate the Metric in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMetric, metric), getPersistedMetric(metric));
    }

    @Test
    @Transactional
    void fullUpdateMetricWithPatch() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metric using partial update
        Metric partialUpdatedMetric = new Metric();
        partialUpdatedMetric.setId(metric.getId());

        partialUpdatedMetric.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetric.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetric))
            )
            .andExpect(status().isOk());

        // Validate the Metric in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricUpdatableFieldsEquals(partialUpdatedMetric, getPersistedMetric(partialUpdatedMetric));
    }

    @Test
    @Transactional
    void patchNonExistingMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, metric.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(metric))
            )
            .andExpect(status().isBadRequest());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metric))
            )
            .andExpect(status().isBadRequest());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMetric() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(metric)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Metric in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetric() throws Exception {
        // Initialize the database
        metricRepository.saveAndFlush(metric);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the metric
        restMetricMockMvc
            .perform(delete(ENTITY_API_URL_ID, metric.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return metricRepository.count();
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

    protected Metric getPersistedMetric(Metric metric) {
        return metricRepository.findById(metric.getId()).orElseThrow();
    }

    protected void assertPersistedMetricToMatchAllProperties(Metric expectedMetric) {
        assertMetricAllPropertiesEquals(expectedMetric, getPersistedMetric(expectedMetric));
    }

    protected void assertPersistedMetricToMatchUpdatableProperties(Metric expectedMetric) {
        assertMetricAllUpdatablePropertiesEquals(expectedMetric, getPersistedMetric(expectedMetric));
    }
}
