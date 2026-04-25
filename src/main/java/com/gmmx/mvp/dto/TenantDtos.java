package com.gmmx.mvp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public class TenantDtos {

    @Data
    @Builder
    public static class TenantLookupResponse {
        private UUID id;
        private String name;
        private String subdomain;
        private String displayName;
        private String logoUrl;
        private String address;
        private String contactPhone;
    }
}
