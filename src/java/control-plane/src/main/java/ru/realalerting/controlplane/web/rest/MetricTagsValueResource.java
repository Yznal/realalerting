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
import ru.realalerting.controlplane.domain.MetricTagsValue;
import ru.realalerting.controlplane.repository.MetricTagsValueRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.MetricTagsValue}.
 */
@RestController
@RequestMapping("/api/metric-tags-values")
@Transactional
public class MetricTagsValueResource {

    private final Logger log = LoggerFactory.getLogger(MetricTagsValueResource.class);

    private static final String ENTITY_NAME = "metricTagsValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetricTagsValueRepository metricTagsValueRepository;

    public MetricTagsValueResource(MetricTagsValueRepository metricTagsValueRepository) {
        this.metricTagsValueRepository = metricTagsValueRepository;
    }

    /**
     * {@code POST  /metric-tags-values} : Create a new metricTagsValue.
     *
     * @param metricTagsValue the metricTagsValue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metricTagsValue, or with status {@code 400 (Bad Request)} if the metricTagsValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MetricTagsValue> createMetricTagsValue(@Valid @RequestBody MetricTagsValue metricTagsValue)
        throws URISyntaxException {
        log.debug("REST request to save MetricTagsValue : {}", metricTagsValue);
        if (metricTagsValue.getId() != null) {
            throw new BadRequestAlertException("A new metricTagsValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        metricTagsValue = metricTagsValueRepository.save(metricTagsValue);
        return ResponseEntity.created(new URI("/api/metric-tags-values/" + metricTagsValue.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, metricTagsValue.getId().toString()))
            .body(metricTagsValue);
    }

    /**
     * {@code PUT  /metric-tags-values/:id} : Updates an existing metricTagsValue.
     *
     * @param id the id of the metricTagsValue to save.
     * @param metricTagsValue the metricTagsValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricTagsValue,
     * or with status {@code 400 (Bad Request)} if the metricTagsValue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metricTagsValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetricTagsValue> updateMetricTagsValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MetricTagsValue metricTagsValue
    ) throws URISyntaxException {
        log.debug("REST request to update MetricTagsValue : {}, {}", id, metricTagsValue);
        if (metricTagsValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricTagsValue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricTagsValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        metricTagsValue = metricTagsValueRepository.save(metricTagsValue);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricTagsValue.getId().toString()))
            .body(metricTagsValue);
    }

    /**
     * {@code PATCH  /metric-tags-values/:id} : Partial updates given fields of an existing metricTagsValue, field will ignore if it is null
     *
     * @param id the id of the metricTagsValue to save.
     * @param metricTagsValue the metricTagsValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricTagsValue,
     * or with status {@code 400 (Bad Request)} if the metricTagsValue is not valid,
     * or with status {@code 404 (Not Found)} if the metricTagsValue is not found,
     * or with status {@code 500 (Internal Server Error)} if the metricTagsValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MetricTagsValue> partialUpdateMetricTagsValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MetricTagsValue metricTagsValue
    ) throws URISyntaxException {
        log.debug("REST request to partial update MetricTagsValue partially : {}, {}", id, metricTagsValue);
        if (metricTagsValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricTagsValue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricTagsValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MetricTagsValue> result = metricTagsValueRepository
            .findById(metricTagsValue.getId())
            .map(existingMetricTagsValue -> {
                if (metricTagsValue.getValue01() != null) {
                    existingMetricTagsValue.setValue01(metricTagsValue.getValue01());
                }
                if (metricTagsValue.getValue256() != null) {
                    existingMetricTagsValue.setValue256(metricTagsValue.getValue256());
                }

                return existingMetricTagsValue;
            })
            .map(metricTagsValueRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricTagsValue.getId().toString())
        );
    }

    /**
     * {@code GET  /metric-tags-values} : get all the metricTagsValues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metricTagsValues in body.
     */
    @GetMapping("")
    public List<MetricTagsValue> getAllMetricTagsValues() {
        log.debug("REST request to get all MetricTagsValues");
        return metricTagsValueRepository.findAll();
    }

    /**
     * {@code GET  /metric-tags-values/:id} : get the "id" metricTagsValue.
     *
     * @param id the id of the metricTagsValue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metricTagsValue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetricTagsValue> getMetricTagsValue(@PathVariable("id") Long id) {
        log.debug("REST request to get MetricTagsValue : {}", id);
        Optional<MetricTagsValue> metricTagsValue = metricTagsValueRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(metricTagsValue);
    }

    /**
     * {@code DELETE  /metric-tags-values/:id} : delete the "id" metricTagsValue.
     *
     * @param id the id of the metricTagsValue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetricTagsValue(@PathVariable("id") Long id) {
        log.debug("REST request to delete MetricTagsValue : {}", id);
        metricTagsValueRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
