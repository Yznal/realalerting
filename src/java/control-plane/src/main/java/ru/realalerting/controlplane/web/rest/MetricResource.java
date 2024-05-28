package ru.realalerting.controlplane.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import ru.realalerting.controlplane.domain.Metric;
import ru.realalerting.controlplane.repository.MetricRepository;
import ru.realalerting.controlplane.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.realalerting.controlplane.domain.Metric}.
 */
@RestController
@RequestMapping("/api/metrics")
@Transactional
public class MetricResource {

    private final Logger log = LoggerFactory.getLogger(MetricResource.class);

    private static final String ENTITY_NAME = "metric";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetricRepository metricRepository;

    public MetricResource(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    /**
     * {@code POST  /metrics} : Create a new metric.
     *
     * @param metric the metric to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metric, or with status {@code 400 (Bad Request)} if the metric has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Metric> createMetric(@Valid @RequestBody Metric metric) throws URISyntaxException {
        log.debug("REST request to save Metric : {}", metric);
        if (metric.getId() != null) {
            throw new BadRequestAlertException("A new metric cannot already have an ID", ENTITY_NAME, "idexists");
        }
        metric = metricRepository.save(metric);
        return ResponseEntity.created(new URI("/api/metrics/" + metric.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, metric.getId().toString()))
            .body(metric);
    }

    /**
     * {@code PUT  /metrics/:id} : Updates an existing metric.
     *
     * @param id the id of the metric to save.
     * @param metric the metric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metric,
     * or with status {@code 400 (Bad Request)} if the metric is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Metric> updateMetric(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Metric metric
    ) throws URISyntaxException {
        log.debug("REST request to update Metric : {}, {}", id, metric);
        if (metric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        metric = metricRepository.save(metric);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metric.getId().toString()))
            .body(metric);
    }

    /**
     * {@code PATCH  /metrics/:id} : Partial updates given fields of an existing metric, field will ignore if it is null
     *
     * @param id the id of the metric to save.
     * @param metric the metric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metric,
     * or with status {@code 400 (Bad Request)} if the metric is not valid,
     * or with status {@code 404 (Not Found)} if the metric is not found,
     * or with status {@code 500 (Internal Server Error)} if the metric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Metric> partialUpdateMetric(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Metric metric
    ) throws URISyntaxException {
        log.debug("REST request to partial update Metric partially : {}, {}", id, metric);
        if (metric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!metricRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Metric> result = metricRepository
            .findById(metric.getId())
            .map(existingMetric -> {
                if (metric.getType() != null) {
                    existingMetric.setType(metric.getType());
                }
                if (metric.getName() != null) {
                    existingMetric.setName(metric.getName());
                }
                if (metric.getDescription() != null) {
                    existingMetric.setDescription(metric.getDescription());
                }
                if (metric.getCriticalAlertProducerAddress() != null) {
                    existingMetric.setCriticalAlertProducerAddress(metric.getCriticalAlertProducerAddress());
                }
                if (metric.getCriticalAlertProducerPort() != null) {
                    existingMetric.setCriticalAlertProducerPort(metric.getCriticalAlertProducerPort());
                }
                if (metric.getCriticalAlertProducerUri() != null) {
                    existingMetric.setCriticalAlertProducerUri(metric.getCriticalAlertProducerUri());
                }
                if (metric.getCriticalAlertProducerStreamId() != null) {
                    existingMetric.setCriticalAlertProducerStreamId(metric.getCriticalAlertProducerStreamId());
                }

                return existingMetric;
            })
            .map(metricRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metric.getId().toString())
        );
    }

    /**
     * {@code GET  /metrics} : get all the metrics.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metrics in body.
     */
    @GetMapping("")
    public List<Metric> getAllMetrics(@RequestParam(name = "filter", required = false) String filter) {
        if ("metrictagsvalue-is-null".equals(filter)) {
            log.debug("REST request to get all Metrics where metricTagsValue is null");
            return StreamSupport.stream(metricRepository.findAll().spliterator(), false)
                .filter(metric -> metric.getMetricTagsValue() == null)
                .toList();
        }
        log.debug("REST request to get all Metrics");
        return metricRepository.findAll();
    }

    /**
     * {@code GET  /metrics/:id} : get the "id" metric.
     *
     * @param id the id of the metric to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metric, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Metric> getMetric(@PathVariable("id") Long id) {
        log.debug("REST request to get Metric : {}", id);
        Optional<Metric> metric = metricRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(metric);
    }

    /**
     * {@code DELETE  /metrics/:id} : delete the "id" metric.
     *
     * @param id the id of the metric to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetric(@PathVariable("id") Long id) {
        log.debug("REST request to delete Metric : {}", id);
        metricRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
