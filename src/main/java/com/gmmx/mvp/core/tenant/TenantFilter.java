package com.gmmx.mvp.core.tenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantIdStr = httpRequest.getHeader(TENANT_HEADER);

        // Subdomain extraction logic could go here
        // String serverName = httpRequest.getServerName();
        // if (serverName.endsWith(".gmmx.app")) { ... }

        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                TenantContext.setTenantId(UUID.fromString(tenantIdStr));
            } catch (IllegalArgumentException e) {
                // Invalid UUID format - ignore or handle error
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
