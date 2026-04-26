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
            String subject = "Verify your Gmmx login";
            String body = """
                <!DOCTYPE html>
                <html>
                  <body style="margin:0; padding:0; background-color:#f6f7fb; font-family:Arial, sans-serif;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="padding:20px;">
                      <tr>
                        <td align="center">
                          <table width="100%" style="max-width:400px; background:#ffffff; border-radius:12px; padding:24px; box-shadow:0 4px 12px rgba(0,0,0,0.05);" cellpadding="0" cellspacing="0">
                            <tr>
                              <td align="center" style="padding-bottom:16px;">
                                <img src="https://api.gmmx.app/logo-gmmx.png" width="80" alt="GMMX" />
                              </td>
                            </tr>
                            <tr>
                              <td align="center" style="font-size:20px; font-weight:600; color:#222;">
                                Verify your login
                              </td>
                            </tr>
                            <tr>
                              <td align="center" style="padding:12px 0; color:#555; font-size:14px;">
                                Use the OTP below to continue. This code expires in 10 minutes.
                              </td>
                            </tr>
                            <tr>
                              <td align="center" style="padding:16px 0;">
                                <div style="
                                  display:inline-block;
                                  padding:14px 24px;
                                  font-size:26px;
                                  letter-spacing:6px;
                                  font-weight:bold;
                                  background:#fff0f3;
                                  color:#FF5C73;
                                  border-radius:8px;
                                  border:1px dashed #FF5C73;
                                ">
                                  %s
                                </div>
                              </td>
                            </tr>
                            <tr>
                              <td align="center" style="font-size:12px; color:#999; padding-top:10px;">
                                If you didn’t request this, you can safely ignore this email.
                              </td>
                            </tr>
                          </table>
                          <div style="margin-top:12px; font-size:12px; color:#aaa;">
                            © 2026 GMMX. All rights reserved.
                          </div>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(otp);
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
