package ru.realalerting.controlplane.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.realalerting.controlplane.domain.RealAlert;
import ru.realalerting.controlplane.repository.RealAlertRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.RealAlert}.
 */
@RestController
@RequestMapping("/api/real-alerts")
@Transactional
public class RealAlertResource {

    private final Logger log = LoggerFactory.getLogger(RealAlertResource.class);

    private static final String ENTITY_NAME = "realAlert";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RealAlertRepository realAlertRepository;

    public RealAlertResource(RealAlertRepository realAlertRepository) {
        this.realAlertRepository = realAlertRepository;
    }

    /**
     * {@code POST  /real-alerts} : Create a new realAlert.
     *
     * @param realAlert the realAlert to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new realAlert, or with status {@code 400 (Bad Request)} if the realAlert has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RealAlert> createRealAlert(@Valid @RequestBody RealAlert realAlert) throws URISyntaxException {
        log.debug("REST request to save RealAlert : {}", realAlert);
        if (realAlert.getId() != null) {
            throw new BadRequestAlertException("A new realAlert cannot already have an ID", ENTITY_NAME, "idexists");
        }
        realAlert = realAlertRepository.save(realAlert);
        return ResponseEntity.created(new URI("/api/real-alerts/" + realAlert.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, realAlert.getId().toString()))
            .body(realAlert);
    }

    /**
     * {@code PUT  /real-alerts/:id} : Updates an existing realAlert.
     *
     * @param id the id of the realAlert to save.
     * @param realAlert the realAlert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated realAlert,
     * or with status {@code 400 (Bad Request)} if the realAlert is not valid,
     * or with status {@code 500 (Internal Server Error)} if the realAlert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RealAlert> updateRealAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RealAlert realAlert
    ) throws URISyntaxException {
        log.debug("REST request to update RealAlert : {}, {}", id, realAlert);
        if (realAlert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, realAlert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!realAlertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        realAlert = realAlertRepository.save(realAlert);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, realAlert.getId().toString()))
            .body(realAlert);
    }

    /**
     * {@code PATCH  /real-alerts/:id} : Partial updates given fields of an existing realAlert, field will ignore if it is null
     *
     * @param id the id of the realAlert to save.
     * @param realAlert the realAlert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated realAlert,
     * or with status {@code 400 (Bad Request)} if the realAlert is not valid,
     * or with status {@code 404 (Not Found)} if the realAlert is not found,
     * or with status {@code 500 (Internal Server Error)} if the realAlert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RealAlert> partialUpdateRealAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RealAlert realAlert
    ) throws URISyntaxException {
        log.debug("REST request to partial update RealAlert partially : {}, {}", id, realAlert);
        if (realAlert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, realAlert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!realAlertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RealAlert> result = realAlertRepository
            .findById(realAlert.getId())
            .map(existingRealAlert -> {
                if (realAlert.getType() != null) {
                    existingRealAlert.setType(realAlert.getType());
                }
                if (realAlert.getName() != null) {
                    existingRealAlert.setName(realAlert.getName());
                }
                if (realAlert.getDescription() != null) {
                    existingRealAlert.setDescription(realAlert.getDescription());
                }
                if (realAlert.getConf() != null) {
                    existingRealAlert.setConf(realAlert.getConf());
                }

                return existingRealAlert;
            })
            .map(realAlertRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, realAlert.getId().toString())
        );
    }

    /**
     * {@code GET  /real-alerts} : get all the realAlerts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of realAlerts in body.
     */
    @GetMapping("")
    public List<RealAlert> getAllRealAlerts() {
        log.debug("REST request to get all RealAlerts");
        return realAlertRepository.findAll();
    }

    /**
     * {@code GET  /real-alerts/:id} : get the "id" realAlert.
     *
     * @param id the id of the realAlert to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the realAlert, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RealAlert> getRealAlert(@PathVariable("id") Long id) {
        log.debug("REST request to get RealAlert : {}", id);
        Optional<RealAlert> realAlert = realAlertRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(realAlert);
    }

    /**
     * {@code DELETE  /real-alerts/:id} : delete the "id" realAlert.
     *
     * @param id the id of the realAlert to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRealAlert(@PathVariable("id") Long id) {
        log.debug("REST request to delete RealAlert : {}", id);
        realAlertRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
