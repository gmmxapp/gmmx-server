package com.gmmx.mvp.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class QrUtils {
    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String generateToken(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getUrlEncoder().encodeToString(hash);
        return data + "." + signature;
    }

    public static boolean validateToken(String token, String secret) {
        try {
            int lastDot = token.lastIndexOf('.');
            if (lastDot == -1) return false;

            String data = token.substring(0, lastDot);
            String signature = token.substring(lastDot + 1);

            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getUrlEncoder().encodeToString(hash);

            return signature.equals(expectedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
