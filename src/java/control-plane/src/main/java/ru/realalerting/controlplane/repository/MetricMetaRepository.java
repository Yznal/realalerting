package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.MetricMeta;

/**
 * Spring Data JPA repository for the MetricMeta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricMetaRepository extends JpaRepository<MetricMeta, Long> {}
