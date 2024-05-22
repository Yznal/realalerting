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
import ru.realalerting.controlplane.domain.MetricMeta;
import ru.realalerting.controlplane.repository.MetricMetaRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.MetricMeta}.
 */
@RestController
@RequestMapping("/api/metric-metas")
@Transactional
public class MetricMetaResource {

    private final Logger log = LoggerFactory.getLogger(MetricMetaResource.class);

    private static final String ENTITY_NAME = "metricMeta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetricMetaRepository metricMetaRepository;

    public MetricMetaResource(MetricMetaRepository metricMetaRepository) {
        this.metricMetaRepository = metricMetaRepository;
    }

    /**
     * {@code POST  /metric-metas} : Create a new metricMeta.
     *
     * @param metricMeta the metricMeta to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metricMeta, or with status {@code 400 (Bad Request)} if the metricMeta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MetricMeta> createMetricMeta(@Valid @RequestBody MetricMeta metricMeta) throws URISyntaxException {
        log.debug("REST request to save MetricMeta : {}", metricMeta);
        if (metricMeta.getId() != null) {
            throw new BadRequestAlertException("A new metricMeta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        metricMeta = metricMetaRepository.save(metricMeta);
        return ResponseEntity.created(new URI("/api/metric-metas/" + metricMeta.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, metricMeta.getId().toString()))
            .body(metricMeta);
    }

    /**
     * {@code PUT  /metric-metas/:id} : Updates an existing metricMeta.
     *
     * @param id the id of the metricMeta to save.
     * @param metricMeta the metricMeta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricMeta,
     * or with status {@code 400 (Bad Request)} if the metricMeta is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metricMeta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetricMeta> updateMetricMeta(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MetricMeta metricMeta
    ) throws URISyntaxException {
        log.debug("REST request to update MetricMeta : {}, {}", id, metricMeta);
        if (metricMeta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricMeta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricMetaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        metricMeta = metricMetaRepository.save(metricMeta);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricMeta.getId().toString()))
            .body(metricMeta);
    }

    /**
     * {@code PATCH  /metric-metas/:id} : Partial updates given fields of an existing metricMeta, field will ignore if it is null
     *
     * @param id the id of the metricMeta to save.
     * @param metricMeta the metricMeta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricMeta,
     * or with status {@code 400 (Bad Request)} if the metricMeta is not valid,
     * or with status {@code 404 (Not Found)} if the metricMeta is not found,
     * or with status {@code 500 (Internal Server Error)} if the metricMeta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MetricMeta> partialUpdateMetricMeta(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MetricMeta metricMeta
    ) throws URISyntaxException {
        log.debug("REST request to partial update MetricMeta partially : {}, {}", id, metricMeta);
        if (metricMeta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricMeta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricMetaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MetricMeta> result = metricMetaRepository
            .findById(metricMeta.getId())
            .map(existingMetricMeta -> {
                if (metricMeta.getLabel01() != null) {
                    existingMetricMeta.setLabel01(metricMeta.getLabel01());
                }
                if (metricMeta.getLabel256() != null) {
                    existingMetricMeta.setLabel256(metricMeta.getLabel256());
                }

                return existingMetricMeta;
            })
            .map(metricMetaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metricMeta.getId().toString())
        );
    }

    /**
     * {@code GET  /metric-metas} : get all the metricMetas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metricMetas in body.
     */
    @GetMapping("")
    public List<MetricMeta> getAllMetricMetas() {
        log.debug("REST request to get all MetricMetas");
        return metricMetaRepository.findAll();
    }

    /**
     * {@code GET  /metric-metas/:id} : get the "id" metricMeta.
     *
     * @param id the id of the metricMeta to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metricMeta, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetricMeta> getMetricMeta(@PathVariable("id") Long id) {
        log.debug("REST request to get MetricMeta : {}", id);
        Optional<MetricMeta> metricMeta = metricMetaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(metricMeta);
    }

    /**
     * {@code DELETE  /metric-metas/:id} : delete the "id" metricMeta.
     *
     * @param id the id of the metricMeta to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetricMeta(@PathVariable("id") Long id) {
        log.debug("REST request to delete MetricMeta : {}", id);
        metricMetaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
