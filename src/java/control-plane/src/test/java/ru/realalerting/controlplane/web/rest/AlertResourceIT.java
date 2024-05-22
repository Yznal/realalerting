package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.AlertAsserts.*;
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
import ru.realalerting.controlplane.domain.Metric;
import ru.realalerting.controlplane.domain.enumeration.AlertType;
import ru.realalerting.controlplane.repository.AlertRepository;

/**
 * Integration tests for the {@link AlertResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlertResourceIT {

    private static final AlertType DEFAULT_TYPE = AlertType.CRITICAL;
    private static final AlertType UPDATED_TYPE = AlertType.REGULAR;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/alerts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlertMockMvc;

    private Alert alert;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alert createEntity(EntityManager em) {
        Alert alert = new Alert().type(DEFAULT_TYPE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        alert.setMetric(metric);
        return alert;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alert createUpdatedEntity(EntityManager em) {
        Alert alert = new Alert().type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createUpdatedEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        alert.setMetric(metric);
        return alert;
    }

    @BeforeEach
    public void initTest() {
        alert = createEntity(em);
    }

    @Test
    @Transactional
    void createAlert() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Alert
        var returnedAlert = om.readValue(
            restAlertMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alert)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Alert.class
        );

        // Validate the Alert in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAlertUpdatableFieldsEquals(returnedAlert, getPersistedAlert(returnedAlert));
    }

    @Test
    @Transactional
    void createAlertWithExistingId() throws Exception {
        // Create the Alert with an existing ID
        alert.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alert)))
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alert.setType(null);

        // Create the Alert, which fails.

        restAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlerts() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        // Get all the alertList
        restAlertMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alert.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        // Get the alert
        restAlertMockMvc
            .perform(get(ENTITY_API_URL_ID, alert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alert.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAlert() throws Exception {
        // Get the alert
        restAlertMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert
        Alert updatedAlert = alertRepository.findById(alert.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlert are not directly saved in db
        em.detach(updatedAlert);
        updatedAlert.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAlert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAlert))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertToMatchAllProperties(updatedAlert);
    }

    @Test
    @Transactional
    void putNonExistingAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(put(ENTITY_API_URL_ID, alert.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alert)))
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alert))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlertWithPatch() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert using partial update
        Alert partialUpdatedAlert = new Alert();
        partialUpdatedAlert.setId(alert.getId());

        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlert))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlert, alert), getPersistedAlert(alert));
    }

    @Test
    @Transactional
    void fullUpdateAlertWithPatch() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert using partial update
        Alert partialUpdatedAlert = new Alert();
        partialUpdatedAlert.setId(alert.getId());

        partialUpdatedAlert.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlert))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertUpdatableFieldsEquals(partialUpdatedAlert, getPersistedAlert(partialUpdatedAlert));
    }

    @Test
    @Transactional
    void patchNonExistingAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alert.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alert))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alert))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alert
        restAlertMockMvc
            .perform(delete(ENTITY_API_URL_ID, alert.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertRepository.count();
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

    protected Alert getPersistedAlert(Alert alert) {
        return alertRepository.findById(alert.getId()).orElseThrow();
    }

    protected void assertPersistedAlertToMatchAllProperties(Alert expectedAlert) {
        assertAlertAllPropertiesEquals(expectedAlert, getPersistedAlert(expectedAlert));
    }

    protected void assertPersistedAlertToMatchUpdatableProperties(Alert expectedAlert) {
        assertAlertAllUpdatablePropertiesEquals(expectedAlert, getPersistedAlert(expectedAlert));
    }
}
