package com.gmmx.mvp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {

    // Simple in-memory store for MVP. For production, use Redis or a DB table.
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String identifier) {
        // For the MVP, we use a static OTP as per the prompt: "as of now static otp after msg91 api"
        String otp = "123456";
        otpStore.put(identifier, otp);
        log.info("Generated OTP {} for identifier {}", otp, identifier);
        // Here you would integrate with msg91 or email service
    }

    public boolean verifyOtp(String identifier, String otp) {
        String storedOtp = otpStore.get(identifier);
        
        // As a fallback for MVP/testing, always accept 123456 if stored isn't found
        if (storedOtp == null && "123456".equals(otp)) {
            return true;
        }

        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(identifier); // consume OTP
            return true;
        }
        return false;
    }
}
