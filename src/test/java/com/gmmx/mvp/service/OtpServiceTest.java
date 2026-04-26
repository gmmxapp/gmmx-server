package com.gmmx.mvp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    private static final Pattern OTP_PATTERN = Pattern.compile("(\\d{6})");

    @Mock
    private EmailService emailService;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(emailService);
    }

    @Test
    void verifyOtpSucceedsAndThenInvalidatesOtp() {
        String email = "member@example.com";
        otpService.generateAndSendOtp(email);

        String otp = extractOtpFromSentEmail();
        assertTrue(otpService.verifyOtp(email, otp));
        assertFalse(otpService.verifyOtp(email, otp));
    }

    @Test
    void verifyOtpLocksAfterMaxFailedAttempts() {
        String email = "member@example.com";
        otpService.generateAndSendOtp(email);

        String otp = extractOtpFromSentEmail();
        for (int i = 0; i < 5; i++) {
            assertFalse(otpService.verifyOtp(email, "000000"));
        }

        assertFalse(otpService.verifyOtp(email, otp));
    }

    private String extractOtpFromSentEmail() {
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(eq("member@example.com"), eq("Gmmx- Your Verification Code"), bodyCaptor.capture());
        Matcher matcher = OTP_PATTERN.matcher(bodyCaptor.getValue());
        assertTrue(matcher.find(), "OTP should be present in email body");
        return matcher.group(1);
    }
}
