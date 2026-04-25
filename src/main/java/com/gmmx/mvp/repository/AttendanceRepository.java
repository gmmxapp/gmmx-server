package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.Attendance;
import com.gmmx.mvp.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByMemberAndDate(UserAccount member, LocalDate date);
    List<Attendance> findByMemberIdOrderByDateDesc(UUID memberId);
}
