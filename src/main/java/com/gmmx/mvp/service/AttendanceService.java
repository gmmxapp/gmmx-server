package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.AttendanceDtos;
import com.gmmx.mvp.entity.Attendance;
import com.gmmx.mvp.entity.AttendanceMode;
import com.gmmx.mvp.entity.MemberProfile;
import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.repository.AttendanceRepository;
import com.gmmx.mvp.repository.MemberProfileRepository;
import com.gmmx.mvp.repository.TenantRepository;
import com.gmmx.mvp.util.GeoUtils;
import com.gmmx.mvp.util.QrUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public AttendanceDtos.AttendanceResponse markAttendance(AttendanceDtos.AttendanceMarkRequest request) {
        UUID memberId = request.getMemberId();
        LocalDate today = LocalDate.now();
        UUID tenantId = com.gmmx.mvp.core.tenant.TenantContext.getTenantId();

        // Check if there's an active session (check-in without check-out) for today
        Optional<Attendance> activeSession = attendanceRepository.findTopByMemberIdAndDateAndCheckOutIsNullOrderByCheckInDesc(memberId, today);

        if (activeSession.isPresent()) {
            // Check out
            Attendance attendance = activeSession.get();
            attendance.setCheckOut(LocalDateTime.now());
            return toResponse(attendanceRepository.save(attendance));
        } else {
            // Check in validation
            MemberProfile member = memberProfileRepository.findByIdAndTenantId(memberId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found in your gym"));

            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gym configuration not found"));

            validateCheckIn(request, tenant);

            Attendance attendance = new Attendance();
            attendance.setMember(member);
            attendance.setCheckIn(LocalDateTime.now());
            attendance.setDate(today);
            attendance.setMethod(request.getMethod());
            return toResponse(attendanceRepository.save(attendance));
        }
    }

    private void validateCheckIn(AttendanceDtos.AttendanceMarkRequest request, Tenant tenant) {
        AttendanceMode mode = tenant.getAttendanceMode();
        if (mode == AttendanceMode.MANUAL) return;

        // Location Check
        if (mode == AttendanceMode.LOCATION_ONLY || mode == AttendanceMode.HYBRID) {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                throw new IllegalArgumentException("Location coordinates are required for this gym.");
            }
            if (tenant.getLatitude() == null || tenant.getLongitude() == null) {
                throw new IllegalStateException("Gym location not configured by owner.");
            }

            double distance = GeoUtils.calculateDistance(
                    request.getLatitude(), request.getLongitude(),
                    tenant.getLatitude(), tenant.getLongitude()
            );

            if (distance > tenant.getAttendanceRadius()) {
                throw new IllegalArgumentException("You are too far from the gym (" + (int)distance + "m). Limit is " + tenant.getAttendanceRadius().intValue() + "m.");
            }
        }

        // QR Check
        if (mode == AttendanceMode.QR_ONLY || mode == AttendanceMode.HYBRID) {
            if (request.getQrToken() == null || request.getQrToken().isEmpty()) {
                throw new IllegalArgumentException("QR Token is required for this gym.");
            }
            if (tenant.getQrSecret() == null) {
                throw new IllegalStateException("QR Secret not configured by owner.");
            }

            if (!QrUtils.validateToken(request.getQrToken(), tenant.getQrSecret())) {
                throw new IllegalArgumentException("Invalid or expired QR code.");
            }

            // Optional: Validate timestamp inside token if needed
            // token format: gymId:timestamp.signature
            String tokenData = request.getQrToken().split("\\.")[0];
            String[] parts = tokenData.split(":");
            if (parts.length == 2) {
                long timestamp = Long.parseLong(parts[1]);
                long now = System.currentTimeMillis() / 1000;
                if (Math.abs(now - timestamp) > 60) { // 60 seconds expiry
                    throw new IllegalArgumentException("QR code has expired. Please scan a fresh one.");
                }
            }
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
