package com.propertyinspection.propertyinspectionworkflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

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

        if (mailSender != null && fromEmail != null && !fromEmail.isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("Your Property Inspection Verification Code: " + otpCode);
                message.setText("Dear " + (username != null ? username : "User") + ",\n\n"
                        + "Your 6-digit one-time login verification code (OTP) for the Property Inspection Portal is:\n\n"
                        + "      " + otpCode + "\n\n"
                        + "This code is valid for 5 minutes. Please do not share this code with anyone.\n\n"
                        + "Best regards,\n"
                        + "Property Inspection Workflow Team");

                mailSender.send(message);
                LOGGER.info("✅ [EMAIL SENT] Verification email successfully delivered via SMTP to {}", toEmail);
            } catch (Exception e) {
                LOGGER.error("❌ [EMAIL ERROR] Failed to send email via SMTP to {}: {}", toEmail, e.getMessage());
            }
        } else {
            LOGGER.info("ℹ️ [EMAIL CONFIG] To deliver to real inboxes, configure spring.mail.username and spring.mail.password in application.properties or environment variables.");
        }
    }
}
