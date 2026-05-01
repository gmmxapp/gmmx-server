package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Table(name = "trainer_profiles")
@Audited
public class TrainerProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    private String specialization;
    private Integer experienceYears;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String availability;

    /**
     * Comma-separated list of permissions granted by the gym owner.
     * Possible values: manage_leads, manage_attendance, admin_access, manager_access, trainer_only
     * Example: "manage_leads,manage_attendance"
     */
    @Column(name = "permissions", columnDefinition = "TEXT", nullable = false)
    private String permissions = "";
}
