package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final OtpService otpService;

    public UserController(UserRepository repository, OtpService otpService) {
        this.repository = repository;
        this.otpService = otpService;
    }

    public static class LoginRequest {
        private String username;
        private String password;
        private Boolean useOtp;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Boolean getUseOtp() { return useOtp; }
        public void setUseOtp(Boolean useOtp) { this.useOtp = useOtp; }
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        if (user.getRole() == User.Role.ADMIN) {
            throw new IllegalArgumentException("Cannot self-register as ADMIN");
        }
        user.setApproved(false);
        return repository.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = repository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // If user is not yet approved, return user object directly so frontend/tests can display approval message
        if (!user.isApproved()) {
            return ResponseEntity.ok(user);
        }

        // If 2FA OTP is explicitly requested
        if (Boolean.TRUE.equals(loginRequest.getUseOtp())) {
            Map<String, Object> otpData = otpService.initiateOtp(user);
            return ResponseEntity.ok(otpData);
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String tempToken = body.get("tempToken");
        String otp = body.get("otp");
        boolean valid = otpService.verifyOtp(tempToken, otp);
        if (!valid) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired OTP code. Please check and try again."));
        }
        String username = otpService.getUsernameForToken(tempToken);
        User user = repository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Map<String, Object> otpData = otpService.initiateOtp(user);
        return ResponseEntity.ok(otpData);
    }

    @GetMapping("/pending")
    public List<User> getPending() {
        return repository.findAll().stream().filter(u -> !u.isApproved()).toList();
    }

    @PutMapping("/{id}/approve")
    public User approve(@PathVariable Long id) {
        User user = repository.findById(id).orElseThrow();
        user.setApproved(true);
        return repository.save(user);
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<?> rejectUserDelete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User registration request has been rejected."));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectUserPut(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User registration request has been rejected."));
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }
}