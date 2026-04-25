package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payments")
@Audited
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 50)
    private String paymentMethod;

    @Column(length = 255)
    private String transactionId;
}
