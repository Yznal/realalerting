package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.Alert;

/**
 * Spring Data JPA repository for the Alert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {}
