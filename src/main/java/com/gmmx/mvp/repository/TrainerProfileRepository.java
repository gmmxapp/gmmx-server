package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, UUID> {
    java.util.Optional<TrainerProfile> findByUserId(UUID userId);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM TrainerProfile t WHERE t.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
}
