package com.example.medicalapp.controller;

import com.example.medicalapp.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// pretend-sending a notification
// no DB or app access

@RestController
@RequestMapping("/mock/notify")
public class MockNotificationController {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationController.class);

    // Third pary=ty notification provider delivery DB simulator
    // in memory store
    private final Map<String, String> store = new ConcurrentHashMap<>();

    @PostMapping
    public Map<String, String> receive(@RequestBody NotificationRequest request) {
        String notifId = "notif-" + UUID.randomUUID().toString().substring(0, 8);
        store.put(notifId, "SENT");

        log.info("[MOCK NOTIFICATION SERVICE] Received | patient='{}' | provider='{}' | time='{}' | id={}",
                request.getPatientName(),
                request.getProviderName(),
                request.getAppointmentDateTime(),
                notifId);

        return Map.of("notifId", notifId, "status", "SENT");
    }

    @GetMapping("/{id}")
    public Map<String, String> status(@PathVariable String id) {
        String status = store.getOrDefault(id, "UNKNOWN");
        log.info("[MOCK NOTIFICATION SERVICE] Status query | id={} | status={}", id, status);
        return Map.of("notifId", id, "status", status);
    }
}