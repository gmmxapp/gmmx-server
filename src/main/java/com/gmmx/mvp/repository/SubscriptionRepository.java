package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Subscription s WHERE s.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
}
