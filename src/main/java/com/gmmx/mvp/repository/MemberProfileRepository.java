package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, UUID> {
    java.util.Optional<MemberProfile> findByUserId(UUID userId);
    java.util.Optional<MemberProfile> findByIdAndTenantId(UUID id, UUID tenantId);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM MemberProfile m WHERE m.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);

    long countByTenantId(UUID tenantId);
}
