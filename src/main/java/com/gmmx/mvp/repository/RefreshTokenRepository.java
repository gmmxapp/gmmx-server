package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.RefreshToken;
import com.gmmx.mvp.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(UserAccount user);

    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM RefreshToken r WHERE r.user.id IN (SELECT u.id FROM UserAccount u WHERE u.tenantId = :tenantId)")
    void deleteByTenantId(UUID tenantId);
}
