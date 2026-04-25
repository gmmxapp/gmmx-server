package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attendance")
@Audited
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberProfile member;

    @Column(nullable = false)
    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private String method = "MANUAL";

    @Column(nullable = false)
    private java.time.LocalDate date;
}
