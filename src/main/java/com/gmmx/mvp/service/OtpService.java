package com.gmmx.mvp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    // Simple in-memory store for MVP. For production, use Redis or a DB table.
    private final EmailService emailService;
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String identifier) {
        // Generate a random 6-digit OTP
        String otp = String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
        otpStore.put(identifier, otp);
        log.info("Generated OTP {} for identifier {}", otp, identifier);

        if (identifier.contains("@")) {
            String subject = "Gmmx- Your Verification Code";
            String body = "Thanks for choosing Gmmx. \n\nYour verification code is: " + "<b>" + otp + "</b>"
                    + "\n\nThis code will expire in 10 minutes.";
            emailService.sendEmail(identifier, subject, body);
        } else {
            // Integration with SMS service goes here
            log.info("SMS integration not implemented. OTP for mobile {}: {}", identifier, otp);
        }
    }

    public boolean verifyOtp(String identifier, String otp) {
        String storedOtp = otpStore.get(identifier);

        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(identifier);
            return true;
        }
        return false;
    }
}
