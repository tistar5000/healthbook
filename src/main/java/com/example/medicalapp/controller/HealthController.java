package com.example.medicalapp.controller;

import com.example.medicalapp.monitoring.BookingMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate   jdbcTemplate;
    private final BookingMetrics metrics;
    private final RestTemplate   restTemplate = new RestTemplate();

    public HealthController(JdbcTemplate jdbcTemplate, BookingMetrics metrics) {
        this.jdbcTemplate = jdbcTemplate;
        this.metrics      = metrics;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> db     = checkDatabase();
        Map<String, Object> notify = checkNotificationService();

        boolean allUp   = "UP".equals(db.get("status")) && "UP".equals(notify.get("status"));
        String  overall = allUp ? "UP" : "DEGRADED";

        Map<String, Object> components = new LinkedHashMap<>();
        components.put("database",            db);
        components.put("notificationService", notify);

        Map<String, Object> metricsMap = new LinkedHashMap<>();
        metricsMap.put("totalBookings",          metrics.getTotalBookings());
        metricsMap.put("failedBookings",          metrics.getFailedBookings());
        metricsMap.put("notificationFailures",    metrics.getNotifFailures());
        metricsMap.put("averageBookingLatencyMs", metrics.getAverageLatencyMs());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",     overall);
        body.put("timestamp",  LocalDateTime.now().toString());
        body.put("components", components);
        body.put("metrics",    metricsMap);

        log.info("[HEALTH CHECK] status={} | db={} | notify={} | totalBookings={} | failedBookings={}",
                overall, db.get("status"), notify.get("status"),
                metrics.getTotalBookings(), metrics.getFailedBookings());

        HttpStatus code = allUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(code).body(body);
    }

    private Map<String, Object> checkDatabase() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long t0 = System.currentTimeMillis();
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long latency = System.currentTimeMillis() - t0;
            result.put("status", "UP");
            result.put("detail", "PostgreSQL reachable — SELECT 1 returned in " + latency + "ms");
        } catch (Exception e) {
            log.error("[HEALTH CHECK] Database DOWN: {}", e.getMessage());
            result.put("status", "DOWN");
            result.put("detail", e.getMessage());
        }
        return result;
    }

    private Map<String, Object> checkNotificationService() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            restTemplate.headForHeaders("http://localhost:8080/mock/notify");
            result.put("status", "UP");
            result.put("detail", "Mock notification service reachable");
        } catch (Exception e) {
            log.warn("[HEALTH CHECK] Notification service DOWN: {}", e.getMessage());
            result.put("status", "DOWN");
            result.put("detail", e.getMessage());
        }
        return result;
    }
}