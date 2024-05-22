package ru.realalerting.controlplane.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.realalerting.controlplane.domain.Tenant;
import ru.realalerting.controlplane.repository.TenantRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.Tenant}.
 */
@RestController
@RequestMapping("/api/tenants")
@Transactional
public class TenantResource {

    private final Logger log = LoggerFactory.getLogger(TenantResource.class);

    private static final String ENTITY_NAME = "tenant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TenantRepository tenantRepository;

    public TenantResource(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * {@code POST  /tenants} : Create a new tenant.
     *
     * @param tenant the tenant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenant, or with status {@code 400 (Bad Request)} if the tenant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) throws URISyntaxException {
        log.debug("REST request to save Tenant : {}", tenant);
        if (tenant.getId() != null) {
            throw new BadRequestAlertException("A new tenant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tenant = tenantRepository.save(tenant);
        return ResponseEntity.created(new URI("/api/tenants/" + tenant.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tenant.getId().toString()))
            .body(tenant);
    }

    /**
     * {@code PUT  /tenants/:id} : Updates an existing tenant.
     *
     * @param id the id of the tenant to save.
     * @param tenant the tenant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenant,
     * or with status {@code 400 (Bad Request)} if the tenant is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tenant tenant)
        throws URISyntaxException {
        log.debug("REST request to update Tenant : {}, {}", id, tenant);
        if (tenant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tenant = tenantRepository.save(tenant);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenant.getId().toString()))
            .body(tenant);
    }

    /**
     * {@code PATCH  /tenants/:id} : Partial updates given fields of an existing tenant, field will ignore if it is null
     *
     * @param id the id of the tenant to save.
     * @param tenant the tenant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenant,
     * or with status {@code 400 (Bad Request)} if the tenant is not valid,
     * or with status {@code 404 (Not Found)} if the tenant is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tenant> partialUpdateTenant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Tenant tenant
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tenant partially : {}, {}", id, tenant);
        if (tenant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tenant> result = tenantRepository
            .findById(tenant.getId())
            .map(existingTenant -> {
                if (tenant.getName() != null) {
                    existingTenant.setName(tenant.getName());
                }
                if (tenant.getDescription() != null) {
                    existingTenant.setDescription(tenant.getDescription());
                }

                return existingTenant;
            })
            .map(tenantRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenant.getId().toString())
        );
    }

    /**
     * {@code GET  /tenants} : get all the tenants.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tenants in body.
     */
    @GetMapping("")
    public List<Tenant> getAllTenants(@RequestParam(name = "filter", required = false) String filter) {
        if ("metricmeta-is-null".equals(filter)) {
            log.debug("REST request to get all Tenants where metricMeta is null");
            return StreamSupport.stream(tenantRepository.findAll().spliterator(), false)
                .filter(tenant -> tenant.getMetricMeta() == null)
                .toList();
        }
        log.debug("REST request to get all Tenants");
        return tenantRepository.findAll();
    }

    /**
     * {@code GET  /tenants/:id} : get the "id" tenant.
     *
     * @param id the id of the tenant to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenant, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable("id") Long id) {
        log.debug("REST request to get Tenant : {}", id);
        Optional<Tenant> tenant = tenantRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tenant);
    }

    /**
     * {@code DELETE  /tenants/:id} : delete the "id" tenant.
     *
     * @param id the id of the tenant to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable("id") Long id) {
        log.debug("REST request to delete Tenant : {}", id);
        tenantRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
