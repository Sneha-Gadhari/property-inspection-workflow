package com.propertyinspection.propertyinspectionworkflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    /**
     * Dispatches a verification email containing a 6-digit OTP security code.
     */
    public void sendOtpEmail(String toEmail, String otpCode, String username) {
        LOGGER.info("========================================================================");
        LOGGER.info("📧 [EMAIL DISPATCH] To: {}", toEmail);
        LOGGER.info("📧 [EMAIL DISPATCH] Subject: Property Inspection Portal - Login Verification Code");
        LOGGER.info("📧 [EMAIL DISPATCH] Dear {},", username != null ? username : "Valued User");
        LOGGER.info("📧 [EMAIL DISPATCH] Your 6-digit login verification code is: [{}]", otpCode);
        LOGGER.info("📧 [EMAIL DISPATCH] This code is valid for 5 minutes. Do not share it.");
        LOGGER.info("========================================================================");
    }
}
