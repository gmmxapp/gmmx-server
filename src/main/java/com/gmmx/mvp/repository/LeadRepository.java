package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    Page<Lead> findAllByTenantId(UUID tenantId, Pageable pageable);
    long countByTenantId(UUID tenantId);
}
