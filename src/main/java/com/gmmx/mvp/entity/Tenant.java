package com.gmmx.mvp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenant_subdomain", columnList = "subdomain")
})
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String subdomain;

    private String displayName;
    private String logoUrl;
    private String address;
    @Column(name = "country_code")
    private String countryCode = "+91";

    @Column(name = "contact_mobile_number")
    private String contactMobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean hasMicrosite = false;

    private String about;
    private String themePrimary = "#ef4444"; // Default red-500

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_mode")
    private AttendanceMode attendanceMode = AttendanceMode.MANUAL;

    private Double latitude;
    private Double longitude;

    @Column(name = "attendance_radius")
    private Double attendanceRadius = 500.0; // In meters

    @Column(name = "qr_secret")
    private String qrSecret;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
