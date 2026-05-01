package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByEmailAndTenantId(String email, UUID tenantId);
    Optional<UserAccount> findByEmailOrMobileNumber(String email, String mobileNumber);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM UserAccount u WHERE (u.email = :email OR u.mobileNumber = :mobileNumber) AND u.tenantId = :tenantId")
    Optional<UserAccount> findByEmailOrMobileNumberAndTenantId(
            @org.springframework.data.repository.query.Param("email") String email, 
            @org.springframework.data.repository.query.Param("mobileNumber") String mobileNumber, 
            @org.springframework.data.repository.query.Param("tenantId") UUID tenantId);

    boolean existsByEmail(String email);
    
    long countByRole(com.gmmx.mvp.entity.UserRole role);
    
    long countByRoleAndTenantId(com.gmmx.mvp.entity.UserRole role, UUID tenantId);
    
    org.springframework.data.domain.Page<UserAccount> findByRole(com.gmmx.mvp.entity.UserRole role, org.springframework.data.domain.Pageable pageable);
    
    long countByTenantId(UUID tenantId);
    java.util.List<UserAccount> findAllByTenantId(UUID tenantId);
    
    java.util.Optional<UserAccount> findByTenantIdAndRole(UUID tenantId, com.gmmx.mvp.entity.UserRole role);
    
    java.util.List<UserAccount> findTop10ByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM UserAccount u WHERE u.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
}
