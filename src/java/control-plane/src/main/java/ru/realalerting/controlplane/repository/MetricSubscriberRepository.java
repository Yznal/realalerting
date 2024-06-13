package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.MetricSubscriber;

/**
 * Spring Data JPA repository for the MetricSubscriber entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricSubscriberRepository extends JpaRepository<MetricSubscriber, Integer> {}
