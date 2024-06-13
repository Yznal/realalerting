package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.MetricTagsValue;

/**
 * Spring Data JPA repository for the MetricTagsValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricTagsValueRepository extends JpaRepository<MetricTagsValue, Long> {}
