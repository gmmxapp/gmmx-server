package com.gmmx.mvp.core.entity;

import com.gmmx.mvp.core.tenant.TenantContext;
import jakarta.persistence.PrePersist;
import java.util.UUID;

public class TenantEntityListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (baseEntity.getTenantId() == null) {
                UUID tenantId = TenantContext.getTenantId();
                if (tenantId != null) {
                    baseEntity.setTenantId(tenantId);
                }
            }
        }
    }
}
