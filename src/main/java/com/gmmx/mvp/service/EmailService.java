package com.gmmx.mvp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${spring.mail.password}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Using sync HTTP method over Port 443 to bypass DigitalOcean SMTP blocks.
    public void sendEmail(String to, String subject, String body) {
        try {
            String url = "https://api.resend.com/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from", "onboarding@resend.dev"); // Replace with your verified Resend domain when ready
            requestBody.put("to", List.of(to));
            requestBody.put("subject", subject);
            requestBody.put("html", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("HTML Email sent successfully via Resend API to {}", to);
            } else {
                log.error("Failed to send HTML email, API returned: {}", response.getBody());
                throw new RuntimeException("Resend API failed: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send HTML email via HTTP to {}, Cause: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}
