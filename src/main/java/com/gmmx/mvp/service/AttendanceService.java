package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.AttendanceDtos;
import com.gmmx.mvp.entity.Attendance;
import com.gmmx.mvp.entity.MemberProfile;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.repository.AttendanceRepository;
import com.gmmx.mvp.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Transactional
    public AttendanceDtos.AttendanceResponse markAttendance(AttendanceDtos.AttendanceMarkRequest request) {
        UUID memberId = request.getMemberId();
        LocalDate today = LocalDate.now();

        // Check if there's an active session (check-in without check-out) for today
        Optional<Attendance> activeSession = attendanceRepository.findTopByMemberIdAndDateAndCheckOutIsNullOrderByCheckInDesc(memberId, today);

        if (activeSession.isPresent()) {
            // Check out
            Attendance attendance = activeSession.get();
            attendance.setCheckOut(LocalDateTime.now());
            return toResponse(attendanceRepository.save(attendance));
        } else {
            // Check in
            MemberProfile member = memberProfileRepository.findByIdAndTenantId(memberId, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found in your gym"));

            Attendance attendance = new Attendance();
            attendance.setMember(member);
            attendance.setCheckIn(LocalDateTime.now());
            attendance.setDate(today);
            attendance.setMethod(request.getMethod());
            return toResponse(attendanceRepository.save(attendance));
        }
    }

    public List<AttendanceDtos.AttendanceResponse> getMemberHistory(UUID memberId) {
        return attendanceRepository.findByMemberIdOrderByDateDesc(memberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AttendanceDtos.AttendanceResponse toResponse(Attendance attendance) {
        AttendanceDtos.AttendanceResponse response = new AttendanceDtos.AttendanceResponse();
        response.setId(attendance.getId());
        response.setMemberId(attendance.getMember().getId());
        response.setMemberName(attendance.getMember().getUser().getFullName());
        response.setCheckIn(attendance.getCheckIn());
        response.setCheckOut(attendance.getCheckOut());
        response.setMethod(attendance.getMethod());
        response.setDate(attendance.getDate());
        return response;
    }
}
