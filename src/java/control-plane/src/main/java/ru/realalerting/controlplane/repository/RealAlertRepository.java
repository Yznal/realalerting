package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.RealAlert;

/**
 * Spring Data JPA repository for the RealAlert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RealAlertRepository extends JpaRepository<RealAlert, Long> {}
