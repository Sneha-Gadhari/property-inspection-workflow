package com.propertyinspection.propertyinspectionworkflow;

import com.propertyinspection.propertyinspectionworkflow.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PropertyInspectionWorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropertyInspectionWorkflowApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedAdmin(UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUsername("demo").isEmpty()) {
				User admin = new User();
				admin.setUsername("demo");
				admin.setPassword("demo123");
				admin.setRole(User.Role.ADMIN);
				admin.setApproved(true);
				userRepository.save(admin);
			}
		};
	}
}