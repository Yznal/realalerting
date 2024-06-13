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
import ru.realalerting.controlplane.domain.AlertSubscriber;
import ru.realalerting.controlplane.repository.AlertSubscriberRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.AlertSubscriber}.
 */
@RestController
@RequestMapping("/api/alert-subscribers")
@Transactional
public class AlertSubscriberResource {

    private final Logger log = LoggerFactory.getLogger(AlertSubscriberResource.class);

    private static final String ENTITY_NAME = "alertSubscriber";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlertSubscriberRepository alertSubscriberRepository;

    public AlertSubscriberResource(AlertSubscriberRepository alertSubscriberRepository) {
        this.alertSubscriberRepository = alertSubscriberRepository;
    }

    /**
     * {@code POST  /alert-subscribers} : Create a new alertSubscriber.
     *
     * @param alertSubscriber the alertSubscriber to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alertSubscriber, or with status {@code 400 (Bad Request)} if the alertSubscriber has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AlertSubscriber> createAlertSubscriber(@Valid @RequestBody AlertSubscriber alertSubscriber)
        throws URISyntaxException {
        log.debug("REST request to save AlertSubscriber : {}", alertSubscriber);
        if (alertSubscriber.getId() != null) {
            throw new BadRequestAlertException("A new alertSubscriber cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alertSubscriber = alertSubscriberRepository.save(alertSubscriber);
        return ResponseEntity.created(new URI("/api/alert-subscribers/" + alertSubscriber.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, alertSubscriber.getId().toString()))
            .body(alertSubscriber);
    }

    /**
     * {@code PUT  /alert-subscribers/:id} : Updates an existing alertSubscriber.
     *
     * @param id the id of the alertSubscriber to save.
     * @param alertSubscriber the alertSubscriber to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertSubscriber,
     * or with status {@code 400 (Bad Request)} if the alertSubscriber is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alertSubscriber couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlertSubscriber> updateAlertSubscriber(
        @PathVariable(value = "id", required = false) final Integer id,
        @Valid @RequestBody AlertSubscriber alertSubscriber
    ) throws URISyntaxException {
        log.debug("REST request to update AlertSubscriber : {}, {}", id, alertSubscriber);
        if (alertSubscriber.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertSubscriber.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertSubscriberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alertSubscriber = alertSubscriberRepository.save(alertSubscriber);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alertSubscriber.getId().toString()))
            .body(alertSubscriber);
    }

    /**
     * {@code PATCH  /alert-subscribers/:id} : Partial updates given fields of an existing alertSubscriber, field will ignore if it is null
     *
     * @param id the id of the alertSubscriber to save.
     * @param alertSubscriber the alertSubscriber to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertSubscriber,
     * or with status {@code 400 (Bad Request)} if the alertSubscriber is not valid,
     * or with status {@code 404 (Not Found)} if the alertSubscriber is not found,
     * or with status {@code 500 (Internal Server Error)} if the alertSubscriber couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AlertSubscriber> partialUpdateAlertSubscriber(
        @PathVariable(value = "id", required = false) final Integer id,
        @NotNull @RequestBody AlertSubscriber alertSubscriber
    ) throws URISyntaxException {
        log.debug("REST request to partial update AlertSubscriber partially : {}, {}", id, alertSubscriber);
        if (alertSubscriber.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertSubscriber.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alertSubscriberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AlertSubscriber> result = alertSubscriberRepository
            .findById(alertSubscriber.getId())
            .map(existingAlertSubscriber -> {
                if (alertSubscriber.getSubscriberAddress() != null) {
                    existingAlertSubscriber.setSubscriberAddress(alertSubscriber.getSubscriberAddress());
                }
                if (alertSubscriber.getSubscriberPort() != null) {
                    existingAlertSubscriber.setSubscriberPort(alertSubscriber.getSubscriberPort());
                }
                if (alertSubscriber.getSubscriberUri() != null) {
                    existingAlertSubscriber.setSubscriberUri(alertSubscriber.getSubscriberUri());
                }
                if (alertSubscriber.getSubscriberStreamId() != null) {
                    existingAlertSubscriber.setSubscriberStreamId(alertSubscriber.getSubscriberStreamId());
                }

                return existingAlertSubscriber;
            })
            .map(alertSubscriberRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, alertSubscriber.getId().toString())
        );
    }

    /**
     * {@code GET  /alert-subscribers} : get all the alertSubscribers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of alertSubscribers in body.
     */
    @GetMapping("")
    public List<AlertSubscriber> getAllAlertSubscribers() {
        log.debug("REST request to get all AlertSubscribers");
        return alertSubscriberRepository.findAll();
    }

    /**
     * {@code GET  /alert-subscribers/:id} : get the "id" alertSubscriber.
     *
     * @param id the id of the alertSubscriber to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alertSubscriber, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertSubscriber> getAlertSubscriber(@PathVariable("id") Integer id) {
        log.debug("REST request to get AlertSubscriber : {}", id);
        Optional<AlertSubscriber> alertSubscriber = alertSubscriberRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(alertSubscriber);
    }

    /**
     * {@code DELETE  /alert-subscribers/:id} : delete the "id" alertSubscriber.
     *
     * @param id the id of the alertSubscriber to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlertSubscriber(@PathVariable("id") Integer id) {
        log.debug("REST request to delete AlertSubscriber : {}", id);
        alertSubscriberRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
