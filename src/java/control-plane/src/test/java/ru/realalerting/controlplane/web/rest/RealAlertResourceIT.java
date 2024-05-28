package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.RealAlertAsserts.*;
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
import ru.realalerting.controlplane.domain.RealAlert;
import ru.realalerting.controlplane.domain.enumeration.AlertType;
import ru.realalerting.controlplane.repository.RealAlertRepository;

/**
 * Integration tests for the {@link RealAlertResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RealAlertResourceIT {

    private static final AlertType DEFAULT_TYPE = AlertType.CRITICAL;
    private static final AlertType UPDATED_TYPE = AlertType.REGULAR;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONF = "AAAAAAAAAA";
    private static final String UPDATED_CONF = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/real-alerts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RealAlertRepository realAlertRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRealAlertMockMvc;

    private RealAlert realAlert;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RealAlert createEntity(EntityManager em) {
        RealAlert realAlert = new RealAlert().type(DEFAULT_TYPE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).conf(DEFAULT_CONF);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        realAlert.setClient(client);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        realAlert.setMetric(metric);
        return realAlert;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RealAlert createUpdatedEntity(EntityManager em) {
        RealAlert realAlert = new RealAlert().type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).conf(UPDATED_CONF);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        realAlert.setClient(client);
        // Add required entity
        Metric metric;
        if (TestUtil.findAll(em, Metric.class).isEmpty()) {
            metric = MetricResourceIT.createUpdatedEntity(em);
            em.persist(metric);
            em.flush();
        } else {
            metric = TestUtil.findAll(em, Metric.class).get(0);
        }
        realAlert.setMetric(metric);
        return realAlert;
    }

    @BeforeEach
    public void initTest() {
        realAlert = createEntity(em);
    }

    @Test
    @Transactional
    void createRealAlert() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RealAlert
        var returnedRealAlert = om.readValue(
            restRealAlertMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(realAlert)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RealAlert.class
        );

        // Validate the RealAlert in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRealAlertUpdatableFieldsEquals(returnedRealAlert, getPersistedRealAlert(returnedRealAlert));
    }

    @Test
    @Transactional
    void createRealAlertWithExistingId() throws Exception {
        // Create the RealAlert with an existing ID
        realAlert.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRealAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(realAlert)))
            .andExpect(status().isBadRequest());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        realAlert.setType(null);

        // Create the RealAlert, which fails.

        restRealAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(realAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRealAlerts() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        // Get all the realAlertList
        restRealAlertMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(realAlert.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].conf").value(hasItem(DEFAULT_CONF)));
    }

    @Test
    @Transactional
    void getRealAlert() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        // Get the realAlert
        restRealAlertMockMvc
            .perform(get(ENTITY_API_URL_ID, realAlert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(realAlert.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.conf").value(DEFAULT_CONF));
    }

    @Test
    @Transactional
    void getNonExistingRealAlert() throws Exception {
        // Get the realAlert
        restRealAlertMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRealAlert() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the realAlert
        RealAlert updatedRealAlert = realAlertRepository.findById(realAlert.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRealAlert are not directly saved in db
        em.detach(updatedRealAlert);
        updatedRealAlert.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).conf(UPDATED_CONF);

        restRealAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRealAlert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRealAlert))
            )
            .andExpect(status().isOk());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRealAlertToMatchAllProperties(updatedRealAlert);
    }

    @Test
    @Transactional
    void putNonExistingRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, realAlert.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(realAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(realAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(realAlert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRealAlertWithPatch() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the realAlert using partial update
        RealAlert partialUpdatedRealAlert = new RealAlert();
        partialUpdatedRealAlert.setId(realAlert.getId());

        partialUpdatedRealAlert.type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        restRealAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRealAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRealAlert))
            )
            .andExpect(status().isOk());

        // Validate the RealAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRealAlertUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRealAlert, realAlert),
            getPersistedRealAlert(realAlert)
        );
    }

    @Test
    @Transactional
    void fullUpdateRealAlertWithPatch() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the realAlert using partial update
        RealAlert partialUpdatedRealAlert = new RealAlert();
        partialUpdatedRealAlert.setId(realAlert.getId());

        partialUpdatedRealAlert.type(UPDATED_TYPE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).conf(UPDATED_CONF);

        restRealAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRealAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRealAlert))
            )
            .andExpect(status().isOk());

        // Validate the RealAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRealAlertUpdatableFieldsEquals(partialUpdatedRealAlert, getPersistedRealAlert(partialUpdatedRealAlert));
    }

    @Test
    @Transactional
    void patchNonExistingRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, realAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(realAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(realAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRealAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        realAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealAlertMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(realAlert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RealAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRealAlert() throws Exception {
        // Initialize the database
        realAlertRepository.saveAndFlush(realAlert);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the realAlert
        restRealAlertMockMvc
            .perform(delete(ENTITY_API_URL_ID, realAlert.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return realAlertRepository.count();
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

    protected RealAlert getPersistedRealAlert(RealAlert realAlert) {
        return realAlertRepository.findById(realAlert.getId()).orElseThrow();
    }

    protected void assertPersistedRealAlertToMatchAllProperties(RealAlert expectedRealAlert) {
        assertRealAlertAllPropertiesEquals(expectedRealAlert, getPersistedRealAlert(expectedRealAlert));
    }

    protected void assertPersistedRealAlertToMatchUpdatableProperties(RealAlert expectedRealAlert) {
        assertRealAlertAllUpdatablePropertiesEquals(expectedRealAlert, getPersistedRealAlert(expectedRealAlert));
    }
}
