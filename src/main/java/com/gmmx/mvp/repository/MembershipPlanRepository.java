package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {
    java.util.List<MembershipPlan> findAllByTenantId(UUID tenantId);
    java.util.Optional<MembershipPlan> findByIdAndTenantId(UUID id, UUID tenantId);
    void deleteByTenantId(UUID tenantId);
}
