package com.hoaxify.ws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserRepository;

@SpringBootApplication
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
            var adminInDB = userRepository.findByEmail("admin@aziztaspatisserie.com");
            if (adminInDB != null) {
                adminInDB.setRole("ADMIN");
                adminInDB.setPassword(passwordEncoder.encode("Admin123!"));
                userRepository.save(adminInDB);
                System.out.println(">>> Admin updated: admin@aziztaspatisserie.com");
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@aziztaspatisserie.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setActive(true);
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println(">>> Admin created: admin@aziztaspatisserie.com");
        };
    }
}