package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.Metric;

/**
 * Spring Data JPA repository for the Metric entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricRepository extends JpaRepository<Metric, Integer> {}
