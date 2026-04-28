package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    java.util.List<Equipment> findAllByTenantId(UUID tenantId);
    java.util.Optional<Equipment> findByIdAndTenantId(UUID id, UUID tenantId);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Equipment e WHERE e.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
}
