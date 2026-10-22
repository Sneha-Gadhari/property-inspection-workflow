package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_ATTEMPTS = 5;

    public static class OtpSession {
        private final String username;
        private final String email;
        private final String otpCode;
        private final long expiryTime;
        private int attempts = 0;

        public OtpSession(String username, String email, String otpCode, long expiryTime) {
            this.username = username;
            this.email = email;
            this.otpCode = otpCode;
            this.expiryTime = expiryTime;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getOtpCode() { return otpCode; }
        public long getExpiryTime() { return expiryTime; }
        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
    }

    private final Map<String, OtpSession> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final EmailService emailService;

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    public Map<String, Object> initiateOtp(User user) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        String tempToken = UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + OTP_VALID_DURATION_MS;

        String targetEmail = (user.getEmail() != null && !user.getEmail().isBlank())
                ? user.getEmail()
                : user.getUsername() + "@propertyinspection.com";

        sessions.put(tempToken, new OtpSession(user.getUsername(), targetEmail, code, expiry));

        emailService.sendOtpEmail(targetEmail, code, user.getUsername());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("requiresOtp", true);
        resp.put("tempToken", tempToken);
        resp.put("email", maskEmail(targetEmail));
        resp.put("username", user.getUsername());

        // Preview of OTP is ONLY provided for the demo account
        if ("demo".equalsIgnoreCase(user.getUsername())) {
            resp.put("devOtp", code);
        }
        return resp;
    }

    public String verifyOtpAndGetUsername(String tempToken, String enteredOtp) {
        if (tempToken == null || enteredOtp == null) return null;
        OtpSession session = sessions.get(tempToken);
        if (session == null) return null;

        if (System.currentTimeMillis() > session.getExpiryTime()) {
            sessions.remove(tempToken);
            return null;
        }

        if (session.getAttempts() >= MAX_ATTEMPTS) {
            sessions.remove(tempToken);
            return null;
        }
        session.incrementAttempts();

        boolean match = session.getOtpCode().equals(enteredOtp.trim());
        if (match) {
            String username = session.getUsername();
            sessions.remove(tempToken);
            return username;
        }
        return null;
    }

    public boolean verifyOtp(String tempToken, String enteredOtp) {
        return verifyOtpAndGetUsername(tempToken, enteredOtp) != null;
    }

    public String getUsernameForToken(String tempToken) {
        OtpSession session = sessions.get(tempToken);
        return session != null ? session.getUsername() : null;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "u***@example.com";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + domain;
    }
}
