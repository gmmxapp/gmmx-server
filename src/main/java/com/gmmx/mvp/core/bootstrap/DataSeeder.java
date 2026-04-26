package com.gmmx.mvp.core.bootstrap;

import com.gmmx.mvp.entity.SubscriptionPlan;
import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.repository.TenantRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create Admin Tenant if not exists
        if (!tenantRepository.existsBySubdomain("admin")) {
            Tenant adminTenant = new Tenant();
            adminTenant.setName("System Admin");
            adminTenant.setSubdomain("admin");
            adminTenant.setDisplayName("Gmmx Admin");
            adminTenant.setPlan(SubscriptionPlan.PRO);
            adminTenant = tenantRepository.save(adminTenant);

            // Create Super Admin User
            UserAccount superAdmin = new UserAccount();
            superAdmin.setTenantId(adminTenant.getId());
            superAdmin.setEmail("dev.gmmx@gmail.com");
            superAdmin.setFullName("Super Admin");
            superAdmin.setMobile("9876543211");
            superAdmin.setPasswordHash(passwordEncoder.encode("3210"));
            superAdmin.setRole(UserRole.SUPER_ADMIN);
            userAccountRepository.save(superAdmin);
            
            System.out.println("Super Admin seeded successfully!");
        }
    }
}
