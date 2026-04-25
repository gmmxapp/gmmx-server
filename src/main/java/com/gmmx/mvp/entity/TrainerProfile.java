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

    @Column(columnDefinition = "jsonb")
    private String availability;
}
