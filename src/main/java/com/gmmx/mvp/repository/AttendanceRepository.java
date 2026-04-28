package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Attendance;
import com.gmmx.mvp.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByMemberAndDate(MemberProfile member, LocalDate date);
    Optional<Attendance> findTopByMemberIdAndDateAndCheckOutIsNullOrderByCheckInDesc(UUID memberId, LocalDate date);
    List<Attendance> findByMemberIdOrderByDateDesc(UUID memberId);
    java.util.List<Attendance> findByTenantIdAndDateBetween(UUID tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    @org.springframework.data.jpa.repository.Modifying
    void deleteByTenantId(UUID tenantId);
}
