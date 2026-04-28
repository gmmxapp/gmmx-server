package com.gmmx.mvp.entity;

import com.gmmx.mvp.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "equipment")
@Audited
public class Equipment extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private String condition; // NEW, GOOD, FAIR, POOR, BROKEN

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;
}
