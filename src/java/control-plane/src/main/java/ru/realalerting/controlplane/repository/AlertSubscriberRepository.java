package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.AlertSubscriber;

/**
 * Spring Data JPA repository for the AlertSubscriber entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertSubscriberRepository extends JpaRepository<AlertSubscriber, Integer> {}
