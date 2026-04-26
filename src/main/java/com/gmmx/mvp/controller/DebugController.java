package com.gmmx.mvp.controller;

import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Profile("dev")
public class DebugController {

    private final UserAccountRepository userAccountRepository;

    @GetMapping("/check-user")
    public String checkUser(@RequestParam String identifier) {
        List<UserAccount> users = userAccountRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("DEBUG_v3_START\n");
        sb.append("Searching for: [").append(identifier).append("]\n");
        sb.append("Total users in DB: ").append(users.size()).append("\n\n");
        
        for (UserAccount u : users) {
            sb.append("USER_ROW: ")
              .append("ID=").append(u.getId())
              .append(", TENANT_ID=").append(u.getTenantId() == null ? "NULL_CRITICAL" : u.getTenantId())
              .append(", EMAIL_MASKED=[").append(maskEmail(u.getEmail())).append("]")
              .append(", MOBILE_MASKED=[").append(maskMobile(u.getMobile())).append("]")
              .append(", ROLE=").append(u.getRole())
              .append("\n");
        }
        
        sb.append("\nDEBUG_v3_END");
        return sb.toString();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "N/A";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "N/A";
        }
        return "****" + mobile.substring(mobile.length() - 4);
    }
}
