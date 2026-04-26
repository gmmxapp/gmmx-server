package com.gmmx.mvp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private static final long OTP_EXPIRY_MS = 10 * 60 * 1000;
    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailService emailService;
    private final Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String identifier) {
        int rawOtp = 100000 + SECURE_RANDOM.nextInt(900000);
        String otp = String.valueOf(rawOtp);
        otpStore.put(identifier, new OtpRecord(otp, System.currentTimeMillis() + OTP_EXPIRY_MS, 0));
        log.info("Generated OTP for identifier {}", identifier);

        if (identifier.contains("@")) {
            String subject = "Gmmx- Your Verification Code";
            String body = "Thanks for choosing Gmmx. \n\nYour verification code is: " + "<b>" + otp + "</b>"
                    + "\n\nThis code will expire in 10 minutes.";
            emailService.sendEmail(identifier, subject, body);
        } else {
            // Integration with SMS service goes here
            log.info("SMS integration not implemented for mobile {}", identifier);
        }
    }

    public boolean verifyOtp(String identifier, String otp) {
        OtpRecord storedOtp = otpStore.get(identifier);

        if (storedOtp == null) {
            return false;
        }

        if (storedOtp.expiresAt() < System.currentTimeMillis()) {
            otpStore.remove(identifier);
            return false;
        }

        if (storedOtp.attempts() >= MAX_ATTEMPTS) {
            otpStore.remove(identifier);
            return false;
        }

        if (storedOtp.code().equals(otp)) {
            otpStore.remove(identifier);
            return true;
        }

        otpStore.put(identifier, storedOtp.withAttempts(storedOtp.attempts() + 1));
        return false;
    }

    private record OtpRecord(String code, long expiresAt, int attempts) {
        private OtpRecord withAttempts(int newAttempts) {
            return new OtpRecord(this.code, this.expiresAt, newAttempts);
        }
    }
}
