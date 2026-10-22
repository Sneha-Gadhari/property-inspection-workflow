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
        assertNotNull(resp.get("devOtp"));
        assertTrue(resp.get("email").toString().contains("@inspection.com"));

        String code = resp.get("devOtp").toString();
        assertEquals(6, code.length());

        verify(emailService).sendOtpEmail(Mockito.eq("sarah@inspection.com"), Mockito.eq(code), Mockito.eq("sarah_inspector"));
    }

    @Test
    void testVerifyOtpSuccessAndNoReplay() {
        User user = new User();
        user.setUsername("john_reviewer");
        user.setEmail("john@inspection.com");

        Map<String, Object> resp = otpService.initiateOtp(user);
        String token = resp.get("tempToken").toString();
        String code = resp.get("devOtp").toString();

        assertEquals("john_reviewer", otpService.getUsernameForToken(token));

        // Correct OTP verification
        boolean verified = otpService.verifyOtp(token, code);
        assertTrue(verified);

        // Cannot replay token once verified
        boolean replay = otpService.verifyOtp(token, code);
        assertFalse(replay);
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
