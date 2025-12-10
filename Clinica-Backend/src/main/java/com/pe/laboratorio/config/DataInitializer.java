package com.pe.laboratorio.config;

import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            // Admin user
            if (repository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                repository.save(admin);
                log.info(">>> Usuario de prueba 'admin' creado exitosamente.");
            }

            // Technologist user
            if (repository.findByUsername("tecnologo").isEmpty()) {
                User tecnologo = new User();
                tecnologo.setUsername("tecnologo");
                tecnologo.setPassword(encoder.encode("tecnologo123"));
                tecnologo.setRole("ROLE_TECNOLOGO");
                repository.save(tecnologo);
                log.info(">>> Usuario de prueba 'tecnologo' creado exitosamente.");
            }

            // Biologist user
            if (repository.findByUsername("biologo").isEmpty()) {
                User biologo = new User();
                biologo.setUsername("biologo");
                biologo.setPassword(encoder.encode("biologo123"));
                biologo.setRole("ROLE_BIOLOGO");
                repository.save(biologo);
                log.info(">>> Usuario de prueba 'biologo' creado exitosamente.");
            }
        };
    }
}