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
import ru.realalerting.controlplane.domain.MetricSubscriber;
import ru.realalerting.controlplane.repository.MetricSubscriberRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.MetricSubscriber}.
 */
@RestController
@RequestMapping("/api/metric-subscribers")
@Transactional
public class MetricSubscriberResource {

    private final Logger log = LoggerFactory.getLogger(MetricSubscriberResource.class);

    private static final String ENTITY_NAME = "metricSubscriber";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetricSubscriberRepository metricSubscriberRepository;

    public MetricSubscriberResource(MetricSubscriberRepository metricSubscriberRepository) {
        this.metricSubscriberRepository = metricSubscriberRepository;
    }

    /**
     * {@code POST  /metric-subscribers} : Create a new metricSubscriber.
     *
     * @param metricSubscriber the metricSubscriber to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metricSubscriber, or with status {@code 400 (Bad Request)} if the metricSubscriber has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MetricSubscriber> createMetricSubscriber(@Valid @RequestBody MetricSubscriber metricSubscriber)
        throws URISyntaxException {
        log.debug("REST request to save MetricSubscriber : {}", metricSubscriber);
        if (metricSubscriber.getId() != null) {
            throw new BadRequestAlertException("A new metricSubscriber cannot already have an ID", ENTITY_NAME, "idexists");
        }
        metricSubscriber = metricSubscriberRepository.save(metricSubscriber);
        return ResponseEntity.created(new URI("/api/metric-subscribers/" + metricSubscriber.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, metricSubscriber.getId().toString()))
            .body(metricSubscriber);
    }

    /**
     * {@code PUT  /metric-subscribers/:id} : Updates an existing metricSubscriber.
     *
     * @param id the id of the metricSubscriber to save.
     * @param metricSubscriber the metricSubscriber to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricSubscriber,
     * or with status {@code 400 (Bad Request)} if the metricSubscriber is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metricSubscriber couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetricSubscriber> updateMetricSubscriber(
        @PathVariable(value = "id", required = false) final Integer id,
        @Valid @RequestBody MetricSubscriber metricSubscriber
    ) throws URISyntaxException {
        log.debug("REST request to update MetricSubscriber : {}, {}", id, metricSubscriber);
        if (metricSubscriber.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricSubscriber.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricSubscriberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        metricSubscriber = metricSubscriberRepository.save(metricSubscriber);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricSubscriber.getId().toString()))
            .body(metricSubscriber);
    }

    /**
     * {@code PATCH  /metric-subscribers/:id} : Partial updates given fields of an existing metricSubscriber, field will ignore if it is null
     *
     * @param id the id of the metricSubscriber to save.
     * @param metricSubscriber the metricSubscriber to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricSubscriber,
     * or with status {@code 400 (Bad Request)} if the metricSubscriber is not valid,
     * or with status {@code 404 (Not Found)} if the metricSubscriber is not found,
     * or with status {@code 500 (Internal Server Error)} if the metricSubscriber couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MetricSubscriber> partialUpdateMetricSubscriber(
        @PathVariable(value = "id", required = false) final Integer id,
        @NotNull @RequestBody MetricSubscriber metricSubscriber
    ) throws URISyntaxException {
        log.debug("REST request to partial update MetricSubscriber partially : {}, {}", id, metricSubscriber);
        if (metricSubscriber.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricSubscriber.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricSubscriberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MetricSubscriber> result = metricSubscriberRepository
            .findById(metricSubscriber.getId())
            .map(existingMetricSubscriber -> {
                if (metricSubscriber.getSubscriberAddress() != null) {
                    existingMetricSubscriber.setSubscriberAddress(metricSubscriber.getSubscriberAddress());
                }
                if (metricSubscriber.getSubscriberPort() != null) {
                    existingMetricSubscriber.setSubscriberPort(metricSubscriber.getSubscriberPort());
                }
                if (metricSubscriber.getSubscriberUri() != null) {
                    existingMetricSubscriber.setSubscriberUri(metricSubscriber.getSubscriberUri());
                }
                if (metricSubscriber.getSubscriberStreamId() != null) {
                    existingMetricSubscriber.setSubscriberStreamId(metricSubscriber.getSubscriberStreamId());
                }

                return existingMetricSubscriber;
            })
            .map(metricSubscriberRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricSubscriber.getId().toString())
        );
    }

    /**
     * {@code GET  /metric-subscribers} : get all the metricSubscribers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metricSubscribers in body.
     */
    @GetMapping("")
    public List<MetricSubscriber> getAllMetricSubscribers() {
        log.debug("REST request to get all MetricSubscribers");
        return metricSubscriberRepository.findAll();
    }

    /**
     * {@code GET  /metric-subscribers/:id} : get the "id" metricSubscriber.
     *
     * @param id the id of the metricSubscriber to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metricSubscriber, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetricSubscriber> getMetricSubscriber(@PathVariable("id") Integer id) {
        log.debug("REST request to get MetricSubscriber : {}", id);
        Optional<MetricSubscriber> metricSubscriber = metricSubscriberRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(metricSubscriber);
    }

    /**
     * {@code DELETE  /metric-subscribers/:id} : delete the "id" metricSubscriber.
     *
     * @param id the id of the metricSubscriber to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetricSubscriber(@PathVariable("id") Integer id) {
        log.debug("REST request to delete MetricSubscriber : {}", id);
        metricSubscriberRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
