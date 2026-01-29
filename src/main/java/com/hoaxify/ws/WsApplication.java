package com.hoaxify.ws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserRepository;

@SpringBootApplication
@EnableAsync
public class WsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WsApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner devUserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            var userInDB = userRepository.findByEmail("user1@mail.com");
            if (userInDB != null) return;
            for (var i = 1; i <= 25; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setEmail("user" + i + "@mail.com");
                user.setPassword(passwordEncoder.encode("P4ssword"));
                user.setActive(true);
                userRepository.save(user);
            }
        };
    }

    @Bean
    @Profile("prod")
    CommandLineRunner prodAdminCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            String adminEmail = "aziz@admin.com";
            var existingAdmin = userRepository.findByEmail(adminEmail);
            if (existingAdmin != null) {
                System.out.println(">>> Admin already exists: " + adminEmail);
                return;
            }

            User admin = new User();
            admin.setUsername("azizadmin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Aziz123!"));
            admin.setActive(true);
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println(">>> NEW Admin created: " + adminEmail + " / Aziz123!");
        };
    }
}