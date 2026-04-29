package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    long countByTenantId(UUID tenantId);
}
