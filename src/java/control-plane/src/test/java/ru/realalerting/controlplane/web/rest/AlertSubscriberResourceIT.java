package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.AlertSubscriberAsserts.*;
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
import ru.realalerting.controlplane.domain.Alert;
import ru.realalerting.controlplane.domain.AlertSubscriber;
import ru.realalerting.controlplane.domain.Client;
import ru.realalerting.controlplane.repository.AlertSubscriberRepository;

/**
 * Integration tests for the {@link AlertSubscriberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlertSubscriberResourceIT {

    private static final String DEFAULT_SUBSCRIBER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIBER_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBSCRIBER_PORT = 1;
    private static final Integer UPDATED_SUBSCRIBER_PORT = 2;

    private static final String DEFAULT_SUBSCRIBER_URI = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIBER_URI = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBSCRIBER_STREAM_ID = 1;
    private static final Integer UPDATED_SUBSCRIBER_STREAM_ID = 2;

    private static final String ENTITY_API_URL = "/api/alert-subscribers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertSubscriberRepository alertSubscriberRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlertSubscriberMockMvc;

    private AlertSubscriber alertSubscriber;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertSubscriber createEntity(EntityManager em) {
        AlertSubscriber alertSubscriber = new AlertSubscriber()
            .subscriberAddress(DEFAULT_SUBSCRIBER_ADDRESS)
            .subscriberPort(DEFAULT_SUBSCRIBER_PORT)
            .subscriberUri(DEFAULT_SUBSCRIBER_URI)
            .subscriberStreamId(DEFAULT_SUBSCRIBER_STREAM_ID);
        // Add required entity
        Alert alert;
        if (TestUtil.findAll(em, Alert.class).isEmpty()) {
            alert = AlertResourceIT.createEntity(em);
            em.persist(alert);
            em.flush();
        } else {
            alert = TestUtil.findAll(em, Alert.class).get(0);
        }
        alertSubscriber.setAlert(alert);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        alertSubscriber.setClient(client);
        return alertSubscriber;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertSubscriber createUpdatedEntity(EntityManager em) {
        AlertSubscriber alertSubscriber = new AlertSubscriber()
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);
        // Add required entity
        Alert alert;
        if (TestUtil.findAll(em, Alert.class).isEmpty()) {
            alert = AlertResourceIT.createUpdatedEntity(em);
            em.persist(alert);
            em.flush();
        } else {
            alert = TestUtil.findAll(em, Alert.class).get(0);
        }
        alertSubscriber.setAlert(alert);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        alertSubscriber.setClient(client);
        return alertSubscriber;
    }

    @BeforeEach
    public void initTest() {
        alertSubscriber = createEntity(em);
    }

    @Test
    @Transactional
    void createAlertSubscriber() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AlertSubscriber
        var returnedAlertSubscriber = om.readValue(
            restAlertSubscriberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertSubscriber)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AlertSubscriber.class
        );

        // Validate the AlertSubscriber in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAlertSubscriberUpdatableFieldsEquals(returnedAlertSubscriber, getPersistedAlertSubscriber(returnedAlertSubscriber));
    }

    @Test
    @Transactional
    void createAlertSubscriberWithExistingId() throws Exception {
        // Create the AlertSubscriber with an existing ID
        alertSubscriber.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlertSubscriberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertSubscriber)))
            .andExpect(status().isBadRequest());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAlertSubscribers() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        // Get all the alertSubscriberList
        restAlertSubscriberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alertSubscriber.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriberAddress").value(hasItem(DEFAULT_SUBSCRIBER_ADDRESS)))
            .andExpect(jsonPath("$.[*].subscriberPort").value(hasItem(DEFAULT_SUBSCRIBER_PORT)))
            .andExpect(jsonPath("$.[*].subscriberUri").value(hasItem(DEFAULT_SUBSCRIBER_URI)))
            .andExpect(jsonPath("$.[*].subscriberStreamId").value(hasItem(DEFAULT_SUBSCRIBER_STREAM_ID)));
    }

    @Test
    @Transactional
    void getAlertSubscriber() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        // Get the alertSubscriber
        restAlertSubscriberMockMvc
            .perform(get(ENTITY_API_URL_ID, alertSubscriber.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alertSubscriber.getId().intValue()))
            .andExpect(jsonPath("$.subscriberAddress").value(DEFAULT_SUBSCRIBER_ADDRESS))
            .andExpect(jsonPath("$.subscriberPort").value(DEFAULT_SUBSCRIBER_PORT))
            .andExpect(jsonPath("$.subscriberUri").value(DEFAULT_SUBSCRIBER_URI))
            .andExpect(jsonPath("$.subscriberStreamId").value(DEFAULT_SUBSCRIBER_STREAM_ID));
    }

    @Test
    @Transactional
    void getNonExistingAlertSubscriber() throws Exception {
        // Get the alertSubscriber
        restAlertSubscriberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlertSubscriber() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertSubscriber
        AlertSubscriber updatedAlertSubscriber = alertSubscriberRepository.findById(alertSubscriber.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlertSubscriber are not directly saved in db
        em.detach(updatedAlertSubscriber);
        updatedAlertSubscriber
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);

        restAlertSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAlertSubscriber.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAlertSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertSubscriberToMatchAllProperties(updatedAlertSubscriber);
    }

    @Test
    @Transactional
    void putNonExistingAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alertSubscriber.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertSubscriber)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlertSubscriberWithPatch() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertSubscriber using partial update
        AlertSubscriber partialUpdatedAlertSubscriber = new AlertSubscriber();
        partialUpdatedAlertSubscriber.setId(alertSubscriber.getId());

        partialUpdatedAlertSubscriber.subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);

        restAlertSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlertSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlertSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the AlertSubscriber in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertSubscriberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAlertSubscriber, alertSubscriber),
            getPersistedAlertSubscriber(alertSubscriber)
        );
    }

    @Test
    @Transactional
    void fullUpdateAlertSubscriberWithPatch() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertSubscriber using partial update
        AlertSubscriber partialUpdatedAlertSubscriber = new AlertSubscriber();
        partialUpdatedAlertSubscriber.setId(alertSubscriber.getId());

        partialUpdatedAlertSubscriber
            .subscriberAddress(UPDATED_SUBSCRIBER_ADDRESS)
            .subscriberPort(UPDATED_SUBSCRIBER_PORT)
            .subscriberUri(UPDATED_SUBSCRIBER_URI)
            .subscriberStreamId(UPDATED_SUBSCRIBER_STREAM_ID);

        restAlertSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlertSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlertSubscriber))
            )
            .andExpect(status().isOk());

        // Validate the AlertSubscriber in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertSubscriberUpdatableFieldsEquals(
            partialUpdatedAlertSubscriber,
            getPersistedAlertSubscriber(partialUpdatedAlertSubscriber)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alertSubscriber.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertSubscriber))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlertSubscriber() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertSubscriber.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertSubscriberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alertSubscriber)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AlertSubscriber in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlertSubscriber() throws Exception {
        // Initialize the database
        alertSubscriberRepository.saveAndFlush(alertSubscriber);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alertSubscriber
        restAlertSubscriberMockMvc
            .perform(delete(ENTITY_API_URL_ID, alertSubscriber.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertSubscriberRepository.count();
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

    protected AlertSubscriber getPersistedAlertSubscriber(AlertSubscriber alertSubscriber) {
        return alertSubscriberRepository.findById(alertSubscriber.getId()).orElseThrow();
    }

    protected void assertPersistedAlertSubscriberToMatchAllProperties(AlertSubscriber expectedAlertSubscriber) {
        assertAlertSubscriberAllPropertiesEquals(expectedAlertSubscriber, getPersistedAlertSubscriber(expectedAlertSubscriber));
    }

    protected void assertPersistedAlertSubscriberToMatchUpdatableProperties(AlertSubscriber expectedAlertSubscriber) {
        assertAlertSubscriberAllUpdatablePropertiesEquals(expectedAlertSubscriber, getPersistedAlertSubscriber(expectedAlertSubscriber));
    }
}
