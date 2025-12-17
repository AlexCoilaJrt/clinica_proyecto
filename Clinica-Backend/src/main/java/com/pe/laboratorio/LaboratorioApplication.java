package com.pe.laboratorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class LaboratorioApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaboratorioApplication.class, args);
    }

}
