package com.example.medicalapp.service;

import com.example.medicalapp.model.NotificationRequest;
import com.example.medicalapp.monitoring.BookingMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final RestTemplate   restTemplate;
    private final BookingMetrics metrics;
    private final String         baseUrl;

    public NotificationService(
            BookingMetrics metrics,
            @Value("${notification.service.base-url}") String baseUrl,
            @Value("${notification.service.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${notification.service.read-timeout-ms}")    int readTimeoutMs) {

        this.metrics  = metrics;
        this.baseUrl  = baseUrl;
        this.restTemplate = buildRestTemplate(connectTimeoutMs, readTimeoutMs);
    }

    // rest template w timeouts
    // accounting for potential thread exhaustion
    private RestTemplate buildRestTemplate(int connectMs, int readMs) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectMs, TimeUnit.MILLISECONDS)
                .setResponseTimeout(readMs, TimeUnit.MILLISECONDS)
                .build();

        HttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }

    // avoiding locking request threads (using  this asynchronous thread pool)
    @Async("notificationExecutor")
    public void sendConfirmationAsync(NotificationRequest request) {
        log.info("[NOTIFICATION SENDING] patient={} | provider={} | url={}",
                request.getPatientName(), request.getProviderName(), baseUrl);
        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(baseUrl, request, Map.class);
            String notifId = (String) response.getBody().get("notifId");
            log.info("[NOTIFICATION SENT] notifId={} | patient={}",
                    notifId, request.getPatientName());
        } catch (ResourceAccessException e) {
            log.warn("[NOTIFICATION TIMEOUT] Service did not respond within timeout | " +
                    "patient={} | error={}", request.getPatientName(), e.getMessage());
            metrics.recordNotifFailure();
        } catch (Exception e) {
            log.warn("[NOTIFICATION FAILED] patient={} | error={}",
                    request.getPatientName(), e.getMessage());
            metrics.recordNotifFailure();
        }
    }

    // synchronous status check for health endpoint
    public Map<String, String> getStatus(String notifId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + notifId, Map.class);
        } catch (Exception e) {
            log.warn("[NOTIFICATION STATUS FAILED] notifId={} | error={}", notifId, e.getMessage());
            return Map.of("notifId", notifId, "status", "UNKNOWN");
        }
    }
}