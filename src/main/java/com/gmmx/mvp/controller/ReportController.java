package com.gmmx.mvp.controller;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.repository.AttendanceRepository;
import com.gmmx.mvp.repository.MemberProfileRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ReportController {

    private final MemberProfileRepository memberProfileRepository;
    private final UserAccountRepository userAccountRepository;
    private final AttendanceRepository attendanceRepository;

    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadMembersReport() {
        UUID tenantId = TenantContext.getTenantId();
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Email,Phone,Plan,Join Date,Expiry Date,Fees Paid,Status\n");

        memberProfileRepository.findAll().stream()
            .filter(m -> tenantId.equals(m.getTenantId()))
            .forEach(member -> {
                String name = sanitize(member.getUser() != null ? member.getUser().getFullName() : "");
                String email = sanitize(member.getUser() != null ? member.getUser().getEmail() : "");
                String phone = sanitize(member.getUser() != null ? member.getUser().getMobile() : "");
                String plan = sanitize(member.getMembershipPlan() != null ? member.getMembershipPlan().getName() : "N/A");
                String joinDate = member.getJoinDate() != null ? member.getJoinDate().toString() : "";
                String expiryDate = member.getExpiryDate() != null ? member.getExpiryDate().toString() : "";
                String feesPaid = member.getFeesPaid() != null ? member.getFeesPaid().toString() : "0";
                String status = member.getStatus() != null ? member.getStatus().name() : "ACTIVE";

                csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    name, email, phone, plan, joinDate, expiryDate, feesPaid, status));
            });

        return buildCsvResponse(csv.toString(), "members_report.csv");
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadPaymentsReport() {
        UUID tenantId = TenantContext.getTenantId();
        StringBuilder csv = new StringBuilder();
        csv.append("Member,Amount,Date,Notes\n");

        // Use member profile fees_paid as payment record (simple approach for MVP)
        memberProfileRepository.findAll().stream()
            .filter(m -> tenantId.equals(m.getTenantId()))
            .filter(m -> m.getFeesPaid() != null && m.getFeesPaid().compareTo(java.math.BigDecimal.ZERO) > 0)
            .forEach(member -> {
                String name = sanitize(member.getUser() != null ? member.getUser().getFullName() : "");
                String amount = member.getFeesPaid() != null ? member.getFeesPaid().toString() : "0";
                String date = member.getJoinDate() != null ? member.getJoinDate().toString() : "";
                String notes = sanitize(member.getFeesNotes());

                csv.append(String.format("%s,%s,%s,%s\n", name, amount, date, notes));
            });

        return buildCsvResponse(csv.toString(), "payments_report.csv");
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadAttendanceReport() {
        UUID tenantId = TenantContext.getTenantId();
        StringBuilder csv = new StringBuilder();
        csv.append("Member,Date,Check In,Check Out,Method\n");

        attendanceRepository.findAll().stream()
            .filter(a -> tenantId.equals(a.getTenantId()))
            .forEach(attendance -> {
                String memberName = "";
                if (attendance.getMember() != null && attendance.getMember().getUser() != null) {
                    memberName = sanitize(attendance.getMember().getUser().getFullName());
                }
                String date = attendance.getDate() != null ? attendance.getDate().toString() : "";
                String checkIn = attendance.getCheckIn() != null ? attendance.getCheckIn().toLocalTime().toString() : "";
                String checkOut = attendance.getCheckOut() != null ? attendance.getCheckOut().toLocalTime().toString() : "";
                String method = attendance.getMethod() != null ? attendance.getMethod() : "MANUAL";

                csv.append(String.format("%s,%s,%s,%s,%s\n", memberName, date, checkIn, checkOut, method));
            });

        return buildCsvResponse(csv.toString(), "attendance_report.csv");
    }

    private ResponseEntity<byte[]> buildCsvResponse(String content, String filename) {
        byte[] bytes = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(bytes.length)
            .body(bytes);
    }

    private String sanitize(String value) {
        if (value == null) return "";
        // Escape commas and quotes in CSV
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
