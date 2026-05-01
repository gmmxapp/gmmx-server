package com.gmmx.mvp.util;

public class PhoneUtils {

    public static String normalizeIdentifier(String identifier) {
        if (identifier == null) return null;
        String normalized = identifier.trim().replaceAll("\\s+", "");
        
        // If it's an email, don't strip anything except whitespace
        if (normalized.contains("@")) {
            return normalized;
        }

        // Strip everything except digits for phone numbers
        normalized = normalized.replaceAll("[^\\d+]", "");

        // Handle Indian mobile numbers
        if (normalized.startsWith("+91")) {
            normalized = normalized.substring(3);
        } else if (normalized.startsWith("91") && normalized.length() == 12) {
            normalized = normalized.substring(2);
        } else if (normalized.startsWith("0") && normalized.length() == 11) {
            normalized = normalized.substring(1);
        }
        
        // Final fallback: just take the last 10 digits if it's a number
        if (normalized.length() > 10 && normalized.matches("\\d+")) {
            normalized = normalized.substring(normalized.length() - 10);
        }
        
        return normalized;
    }
}
