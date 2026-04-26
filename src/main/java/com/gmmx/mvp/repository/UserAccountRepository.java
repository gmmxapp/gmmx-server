package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByEmailOrMobile(String email, String mobile);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM UserAccount u WHERE (u.email = :email OR u.mobile = :mobile) AND u.tenantId = :tenantId")
    Optional<UserAccount> findByEmailOrMobileAndTenantId(String email, String mobile, UUID tenantId);

    boolean existsByEmail(String email);
    
    long countByRole(com.gmmx.mvp.entity.UserRole role);
    
    org.springframework.data.domain.Page<UserAccount> findByRole(com.gmmx.mvp.entity.UserRole role, org.springframework.data.domain.Pageable pageable);
    
    long countByTenantId(UUID tenantId);
    
    java.util.Optional<UserAccount> findByTenantIdAndRole(UUID tenantId, com.gmmx.mvp.entity.UserRole role);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM UserAccount u WHERE u.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
}
