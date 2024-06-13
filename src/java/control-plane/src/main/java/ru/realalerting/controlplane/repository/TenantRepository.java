package ru.realalerting.controlplane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.realalerting.controlplane.domain.Tenant;

/**
 * Spring Data JPA repository for the Tenant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {}
