package com.example.medicalapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// >>> point of entry <<<

@SpringBootApplication
@EnableAsync
public class MedicalAppApplication {
    // async task execution for notification service to run on its own thread
    // (prevents request blocking)
    private static final Logger log = LoggerFactory.getLogger(MedicalAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MedicalAppApplication.class, args);
        log.info("[SUCCESS] - HealthBook started on port 8080");
    }
}
