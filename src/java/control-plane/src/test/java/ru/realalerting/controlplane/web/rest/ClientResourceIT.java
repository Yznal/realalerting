package ru.realalerting.controlplane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.realalerting.controlplane.domain.ClientAsserts.*;
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
import ru.realalerting.controlplane.domain.Tenant;
import ru.realalerting.controlplane.repository.ClientRepository;

/**
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final String DEFAULT_PROTOCOL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PROTOCOL_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_PROTOCOL_PORT = 1;
    private static final Integer UPDATED_PROTOCOL_PORT = 2;

    private static final String DEFAULT_PROTOCOL_URI = "AAAAAAAAAA";
    private static final String UPDATED_PROTOCOL_URI = "BBBBBBBBBB";

    private static final Integer DEFAULT_PROTOCOL_STREAM_ID = 1;
    private static final Integer UPDATED_PROTOCOL_STREAM_ID = 2;

    private static final String DEFAULT_METRIC_PRODUCER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_METRIC_PRODUCER_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_METRIC_PRODUCER_PORT = 1;
    private static final Integer UPDATED_METRIC_PRODUCER_PORT = 2;

    private static final String DEFAULT_METRIC_PRODUCER_URI = "AAAAAAAAAA";
    private static final String UPDATED_METRIC_PRODUCER_URI = "BBBBBBBBBB";

    private static final Integer DEFAULT_METRIC_PRODUCER_STREAM_ID = 1;
    private static final Integer UPDATED_METRIC_PRODUCER_STREAM_ID = 2;

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity(EntityManager em) {
        Client client = new Client()
            .protocolAddress(DEFAULT_PROTOCOL_ADDRESS)
            .protocolPort(DEFAULT_PROTOCOL_PORT)
            .protocolUri(DEFAULT_PROTOCOL_URI)
            .protocolStreamId(DEFAULT_PROTOCOL_STREAM_ID)
            .metricProducerAddress(DEFAULT_METRIC_PRODUCER_ADDRESS)
            .metricProducerPort(DEFAULT_METRIC_PRODUCER_PORT)
            .metricProducerUri(DEFAULT_METRIC_PRODUCER_URI)
            .metricProducerStreamId(DEFAULT_METRIC_PRODUCER_STREAM_ID);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        client.setTenant(tenant);
        return client;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity(EntityManager em) {
        Client client = new Client()
            .protocolAddress(UPDATED_PROTOCOL_ADDRESS)
            .protocolPort(UPDATED_PROTOCOL_PORT)
            .protocolUri(UPDATED_PROTOCOL_URI)
            .protocolStreamId(UPDATED_PROTOCOL_STREAM_ID)
            .metricProducerAddress(UPDATED_METRIC_PRODUCER_ADDRESS)
            .metricProducerPort(UPDATED_METRIC_PRODUCER_PORT)
            .metricProducerUri(UPDATED_METRIC_PRODUCER_URI)
            .metricProducerStreamId(UPDATED_METRIC_PRODUCER_STREAM_ID);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity(em);
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        client.setTenant(tenant);
        return client;
    }

    @BeforeEach
    public void initTest() {
        client = createEntity(em);
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Client
        var returnedClient = om.readValue(
            restClientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Client.class
        );

        // Validate the Client in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientUpdatableFieldsEquals(returnedClient, getPersistedClient(returnedClient));
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].protocolAddress").value(hasItem(DEFAULT_PROTOCOL_ADDRESS)))
            .andExpect(jsonPath("$.[*].protocolPort").value(hasItem(DEFAULT_PROTOCOL_PORT)))
            .andExpect(jsonPath("$.[*].protocolUri").value(hasItem(DEFAULT_PROTOCOL_URI)))
            .andExpect(jsonPath("$.[*].protocolStreamId").value(hasItem(DEFAULT_PROTOCOL_STREAM_ID)))
            .andExpect(jsonPath("$.[*].metricProducerAddress").value(hasItem(DEFAULT_METRIC_PRODUCER_ADDRESS)))
            .andExpect(jsonPath("$.[*].metricProducerPort").value(hasItem(DEFAULT_METRIC_PRODUCER_PORT)))
            .andExpect(jsonPath("$.[*].metricProducerUri").value(hasItem(DEFAULT_METRIC_PRODUCER_URI)))
            .andExpect(jsonPath("$.[*].metricProducerStreamId").value(hasItem(DEFAULT_METRIC_PRODUCER_STREAM_ID)));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.protocolAddress").value(DEFAULT_PROTOCOL_ADDRESS))
            .andExpect(jsonPath("$.protocolPort").value(DEFAULT_PROTOCOL_PORT))
            .andExpect(jsonPath("$.protocolUri").value(DEFAULT_PROTOCOL_URI))
            .andExpect(jsonPath("$.protocolStreamId").value(DEFAULT_PROTOCOL_STREAM_ID))
            .andExpect(jsonPath("$.metricProducerAddress").value(DEFAULT_METRIC_PRODUCER_ADDRESS))
            .andExpect(jsonPath("$.metricProducerPort").value(DEFAULT_METRIC_PRODUCER_PORT))
            .andExpect(jsonPath("$.metricProducerUri").value(DEFAULT_METRIC_PRODUCER_URI))
            .andExpect(jsonPath("$.metricProducerStreamId").value(DEFAULT_METRIC_PRODUCER_STREAM_ID));
    }

    @Test
    @Transactional
    void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .protocolAddress(UPDATED_PROTOCOL_ADDRESS)
            .protocolPort(UPDATED_PROTOCOL_PORT)
            .protocolUri(UPDATED_PROTOCOL_URI)
            .protocolStreamId(UPDATED_PROTOCOL_STREAM_ID)
            .metricProducerAddress(UPDATED_METRIC_PRODUCER_ADDRESS)
            .metricProducerPort(UPDATED_METRIC_PRODUCER_PORT)
            .metricProducerUri(UPDATED_METRIC_PRODUCER_URI)
            .metricProducerStreamId(UPDATED_METRIC_PRODUCER_STREAM_ID);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientToMatchAllProperties(updatedClient);
    }

    @Test
    @Transactional
    void putNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL_ID, client.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .protocolAddress(UPDATED_PROTOCOL_ADDRESS)
            .protocolStreamId(UPDATED_PROTOCOL_STREAM_ID)
            .metricProducerStreamId(UPDATED_METRIC_PRODUCER_STREAM_ID);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClient, client), getPersistedClient(client));
    }

    @Test
    @Transactional
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .protocolAddress(UPDATED_PROTOCOL_ADDRESS)
            .protocolPort(UPDATED_PROTOCOL_PORT)
            .protocolUri(UPDATED_PROTOCOL_URI)
            .protocolStreamId(UPDATED_PROTOCOL_STREAM_ID)
            .metricProducerAddress(UPDATED_METRIC_PRODUCER_ADDRESS)
            .metricProducerPort(UPDATED_METRIC_PRODUCER_PORT)
            .metricProducerUri(UPDATED_METRIC_PRODUCER_URI)
            .metricProducerStreamId(UPDATED_METRIC_PRODUCER_STREAM_ID);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(partialUpdatedClient, getPersistedClient(partialUpdatedClient));
    }

    @Test
    @Transactional
    void patchNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, client.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(client)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the client
        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientRepository.count();
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

    protected Client getPersistedClient(Client client) {
        return clientRepository.findById(client.getId()).orElseThrow();
    }

    protected void assertPersistedClientToMatchAllProperties(Client expectedClient) {
        assertClientAllPropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }

    protected void assertPersistedClientToMatchUpdatableProperties(Client expectedClient) {
        assertClientAllUpdatablePropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }
}
