package com.example.medicalapp.controller;

import com.example.medicalapp.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// acts as a cover/proxy layer for internal URL to pass only relevant obscured data to mock service
@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) { this.notificationService = notificationService; }

    @GetMapping("/status/{id}")
    public Map<String, String> getStatus(@PathVariable String id) { return notificationService.getStatus(id); }
}