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
import ru.realalerting.controlplane.domain.Alert;
import ru.realalerting.controlplane.repository.AlertRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.Alert}.
 */
@RestController
@RequestMapping("/api/alerts")
@Transactional
public class AlertResource {

    private final Logger log = LoggerFactory.getLogger(AlertResource.class);

    private static final String ENTITY_NAME = "alert";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlertRepository alertRepository;

    public AlertResource(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    /**
     * {@code POST  /alerts} : Create a new alert.
     *
     * @param alert the alert to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alert, or with status {@code 400 (Bad Request)} if the alert has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Alert> createAlert(@Valid @RequestBody Alert alert) throws URISyntaxException {
        log.debug("REST request to save Alert : {}", alert);
        if (alert.getId() != null) {
            throw new BadRequestAlertException("A new alert cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alert = alertRepository.save(alert);
        return ResponseEntity.created(new URI("/api/alerts/" + alert.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, alert.getId().toString()))
            .body(alert);
    }

    /**
     * {@code PUT  /alerts/:id} : Updates an existing alert.
     *
     * @param id the id of the alert to save.
     * @param alert the alert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alert,
     * or with status {@code 400 (Bad Request)} if the alert is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Alert alert)
        throws URISyntaxException {
        log.debug("REST request to update Alert : {}, {}", id, alert);
        if (alert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alert = alertRepository.save(alert);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alert.getId().toString()))
            .body(alert);
    }

    /**
     * {@code PATCH  /alerts/:id} : Partial updates given fields of an existing alert, field will ignore if it is null
     *
     * @param id the id of the alert to save.
     * @param alert the alert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alert,
     * or with status {@code 400 (Bad Request)} if the alert is not valid,
     * or with status {@code 404 (Not Found)} if the alert is not found,
     * or with status {@code 500 (Internal Server Error)} if the alert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Alert> partialUpdateAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Alert alert
    ) throws URISyntaxException {
        log.debug("REST request to partial update Alert partially : {}, {}", id, alert);
        if (alert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Alert> result = alertRepository
            .findById(alert.getId())
            .map(existingAlert -> {
                if (alert.getType() != null) {
                    existingAlert.setType(alert.getType());
                }
                if (alert.getName() != null) {
                    existingAlert.setName(alert.getName());
                }
                if (alert.getDescription() != null) {
                    existingAlert.setDescription(alert.getDescription());
                }

                return existingAlert;
            })
            .map(alertRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alert.getId().toString())
        );
    }

    /**
     * {@code GET  /alerts} : get all the alerts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of alerts in body.
     */
    @GetMapping("")
    public List<Alert> getAllAlerts() {
        log.debug("REST request to get all Alerts");
        return alertRepository.findAll();
    }

    /**
     * {@code GET  /alerts/:id} : get the "id" alert.
     *
     * @param id the id of the alert to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alert, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlert(@PathVariable("id") Long id) {
        log.debug("REST request to get Alert : {}", id);
        Optional<Alert> alert = alertRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(alert);
    }

    /**
     * {@code DELETE  /alerts/:id} : delete the "id" alert.
     *
     * @param id the id of the alert to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable("id") Long id) {
        log.debug("REST request to delete Alert : {}", id);
        alertRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
