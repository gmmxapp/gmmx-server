package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "subscriptions")
@Audited
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan planType;

    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    @Column(nullable = false)
    private boolean active = true;
}
