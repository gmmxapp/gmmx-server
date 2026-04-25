package com.gmmx.mvp.controller;

import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
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
              .append(", EMAIL=[").append(u.getEmail()).append("]")
              .append(", MOBILE=[").append(u.getMobile()).append("]")
              .append(", ROLE=").append(u.getRole())
              .append("\n");
        }
        
        sb.append("\nDEBUG_v3_END");
        return sb.toString();
    }
}
