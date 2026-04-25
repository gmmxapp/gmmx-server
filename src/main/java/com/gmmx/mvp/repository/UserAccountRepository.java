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
    boolean existsByEmail(String email);
    
    long countByRole(com.gmmx.mvp.entity.UserRole role);
    
    org.springframework.data.domain.Page<UserAccount> findByRole(com.gmmx.mvp.entity.UserRole role, org.springframework.data.domain.Pageable pageable);
}
