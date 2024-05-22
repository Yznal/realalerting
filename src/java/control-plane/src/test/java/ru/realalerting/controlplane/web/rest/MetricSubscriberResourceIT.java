package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.MetricSubscriberAsserts.*;
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
import ru.realalerting.controlplane.domain.MetricSubscriber;
import ru.realalerting.controlplane.repository.MetricSubscriberRepository;

/**
 * Integration tests for the {@link MetricSubscriberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MetricSubscriberResourceIT {

    private static final String DEFAULT_SUBSCRIBER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIBER_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBSCRIBER_PORT = 1;
    private static final Integer UPDATED_SUBSCRIBER_PORT = 2;

    private static final String DEFAULT_SUBSCRIBER_URI = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIBER_URI = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBSCRIBER_STREAM_ID = 1;
    private static final Integer UPDATED_SUBSCRIBER_STREAM_ID = 2;

    private static final String ENTITY_API_URL = "/api/metric-subscribers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricSubscriberRepository metricSubscriberRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetricSubscriberMockMvc;

    private MetricSubscriber metricSubscriber;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricSubscriber createEntity(EntityManager em) {
        MetricSubscriber metricSubscriber = new MetricSubscriber()
            .subscriberAddress(DEFAULT_SUBSCRIBER_ADDRESS)
            .subscriberPort(DEFAULT_SUBSCRIBER_PORT)
            .subscriberUri(DEFAULT_SUBSCRIBER_URI)
            .subscriberStreamId(DEFAULT_SUBSCRIBER_STREAM_ID);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        metricSubscriber.setClient(client);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        metricSubscriber.setMetric(metric);
        return metricSubscriber;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricSubscriber createUpdatedEntity(EntityManager em) {
        MetricSubscriber metricSubscriber = new MetricSubscriber()
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        metricSubscriber.setClient(client);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createUpdatedEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        metricSubscriber.setMetric(metric);
        return metricSubscriber;
    }

    @BeforeEach
    public void initTest() {
        metricSubscriber = createEntity(em);
    }

    @Test
    @Transactional
    void createMetricSubscriber() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MetricSubscriber
        var returnedMetricSubscriber = om.readValue(
            restMetricSubscriberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricSubscriber)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MetricSubscriber.class
        );

        // Validate the MetricSubscriber in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMetricSubscriberUpdatableFieldsEquals(returnedMetricSubscriber, getPersistedMetricSubscriber(returnedMetricSubscriber));
    }

    @Test
    @Transactional
    void createMetricSubscriberWithExistingId() throws Exception {
        // Create the MetricSubscriber with an existing ID
        metricSubscriber.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetricSubscriberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricSubscriber)))
            .andExpect(status().isBadRequest());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMetricSubscribers() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        // Get all the metricSubscriberList
        restMetricSubscriberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metricSubscriber.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriberAddress").value(hasItem(DEFAULT_SUBSCRIBER_ADDRESS)))
            .andExpect(jsonPath("$.[*].subscriberPort").value(hasItem(DEFAULT_SUBSCRIBER_PORT)))
            .andExpect(jsonPath("$.[*].subscriberUri").value(hasItem(DEFAULT_SUBSCRIBER_URI)))
            .andExpect(jsonPath("$.[*].subscriberStreamId").value(hasItem(DEFAULT_SUBSCRIBER_STREAM_ID)));
    }

    @Test
    @Transactional
    void getMetricSubscriber() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        // Get the metricSubscriber
        restMetricSubscriberMockMvc
            .perform(get(ENTITY_API_URL_ID, metricSubscriber.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metricSubscriber.getId().intValue()))
            .andExpect(jsonPath("$.subscriberAddress").value(DEFAULT_SUBSCRIBER_ADDRESS))
            .andExpect(jsonPath("$.subscriberPort").value(DEFAULT_SUBSCRIBER_PORT))
            .andExpect(jsonPath("$.subscriberUri").value(DEFAULT_SUBSCRIBER_URI))
            .andExpect(jsonPath("$.subscriberStreamId").value(DEFAULT_SUBSCRIBER_STREAM_ID));
    }

    @Test
    @Transactional
    void getNonExistingMetricSubscriber() throws Exception {
        // Get the metricSubscriber
        restMetricSubscriberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMetricSubscriber() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricSubscriber
        MetricSubscriber updatedMetricSubscriber = metricSubscriberRepository.findById(metricSubscriber.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetricSubscriber are not directly saved in db
        em.detach(updatedMetricSubscriber);
        updatedMetricSubscriber
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);

        restMetricSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMetricSubscriber.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMetricSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMetricSubscriberToMatchAllProperties(updatedMetricSubscriber);
    }

    @Test
    @Transactional
    void putNonExistingMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, metricSubscriber.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metricSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(metricSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(metricSubscriber)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMetricSubscriberWithPatch() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricSubscriber using partial update
        MetricSubscriber partialUpdatedMetricSubscriber = new MetricSubscriber();
        partialUpdatedMetricSubscriber.setId(metricSubscriber.getId());

        partialUpdatedMetricSubscriber.subscriberPort(UPDATED_SUBSCRIBER_PORT).subscriberUri(UPDATED_SUBSCRIBER_URI);

        restMetricSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the MetricSubscriber in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricSubscriberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMetricSubscriber, metricSubscriber),
            getPersistedMetricSubscriber(metricSubscriber)
        );
    }

    @Test
    @Transactional
    void fullUpdateMetricSubscriberWithPatch() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricSubscriber using partial update
        MetricSubscriber partialUpdatedMetricSubscriber = new MetricSubscriber();
        partialUpdatedMetricSubscriber.setId(metricSubscriber.getId());

        partialUpdatedMetricSubscriber
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);

        restMetricSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMetricSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMetricSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the MetricSubscriber in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricSubscriberUpdatableFieldsEquals(
            partialUpdatedMetricSubscriber,
            getPersistedMetricSubscriber(partialUpdatedMetricSubscriber)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, metricSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(metricSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMetricSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMetricSubscriberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(metricSubscriber)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MetricSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetricSubscriber() throws Exception {
        // Initialize the database
        metricSubscriberRepository.saveAndFlush(metricSubscriber);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the metricSubscriber
        restMetricSubscriberMockMvc
            .perform(delete(ENTITY_API_URL_ID, metricSubscriber.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return metricSubscriberRepository.count();
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

    protected MetricSubscriber getPersistedMetricSubscriber(MetricSubscriber metricSubscriber) {
        return metricSubscriberRepository.findById(metricSubscriber.getId()).orElseThrow();
    }

    protected void assertPersistedMetricSubscriberToMatchAllProperties(MetricSubscriber expectedMetricSubscriber) {
        assertMetricSubscriberAllPropertiesEquals(expectedMetricSubscriber, getPersistedMetricSubscriber(expectedMetricSubscriber));
    }

    protected void assertPersistedMetricSubscriberToMatchUpdatableProperties(MetricSubscriber expectedMetricSubscriber) {
        assertMetricSubscriberAllUpdatablePropertiesEquals(
            expectedMetricSubscriber,
            getPersistedMetricSubscriber(expectedMetricSubscriber)
        );
    }
}
