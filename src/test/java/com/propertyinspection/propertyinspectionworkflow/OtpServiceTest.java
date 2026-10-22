package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

class OtpServiceTest {

    private EmailService emailService;
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        emailService = Mockito.mock(EmailService.class);
        otpService = new OtpService(emailService);
    }

    @Test
    void testInitiateOtpGeneratesValidSessionAndSendsEmail() {
        User user = new User();
        user.setUsername("sarah_inspector");
        user.setEmail("sarah@inspection.com");

        Map<String, Object> resp = otpService.initiateOtp(user);
        assertNotNull(resp);
        assertEquals(true, resp.get("requiresOtp"));
        assertNotNull(resp.get("tempToken"));
        // Non-demo users do NOT receive devOtp preview
        assertNull(resp.get("devOtp"));
        assertTrue(resp.get("email").toString().contains("@inspection.com"));

        // Demo user DOES receive devOtp preview
        User demoUser = new User();
        demoUser.setUsername("demo");
        Map<String, Object> demoResp = otpService.initiateOtp(demoUser);
        assertNotNull(demoResp.get("devOtp"));
        assertEquals(6, demoResp.get("devOtp").toString().length());
    }

    @Test
    void testVerifyOtpSuccessAndNoReplay() {
        User demoUser = new User();
        demoUser.setUsername("demo");
        demoUser.setEmail("demo@inspection.com");

        Map<String, Object> resp = otpService.initiateOtp(demoUser);
        String token = resp.get("tempToken").toString();
        String code = resp.get("devOtp").toString();

        assertEquals("demo", otpService.getUsernameForToken(token));

        // Correct OTP verification
        boolean verified = otpService.verifyOtp(token, code);
        assertTrue(verified);

        // Cannot replay token once verified
        boolean replay = otpService.verifyOtp(token, code);
        assertFalse(replay);

        // verifyOtpAndGetUsername returns username and invalidates token
        Map<String, Object> resp2 = otpService.initiateOtp(demoUser);
        String token2 = resp2.get("tempToken").toString();
        String code2 = resp2.get("devOtp").toString();
        String verifiedUsername = otpService.verifyOtpAndGetUsername(token2, code2);
        assertEquals("demo", verifiedUsername);
    }

    @Test
    void testVerifyOtpFailsOnInvalidCode() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@inspection.com");

        Map<String, Object> resp = otpService.initiateOtp(user);
        String token = resp.get("tempToken").toString();

        boolean verified = otpService.verifyOtp(token, "000000");
        assertFalse(verified);
    }
}
