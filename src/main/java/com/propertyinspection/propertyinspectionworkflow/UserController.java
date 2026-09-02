package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
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
    public User login(@RequestBody User loginRequest) {
        User user = repository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
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

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }
}