package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "leads")
@EqualsAndHashCode(callSuper = true)
public class Lead extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String mobile;

    private String email;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status = LeadStatus.NEW;

    @Enumerated(EnumType.STRING)
    private LeadSource source;

    private String interestLevel; // High, Medium, Low

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_trainer_id")
    private TrainerProfile assignedTrainer;

    private java.time.LocalDate trialDate;

    private boolean isTrialCompleted = false;
}
