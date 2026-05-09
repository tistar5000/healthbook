package com.example.medicalapp.model;

import java.time.LocalDateTime;

public class AvailabilitySlot {
    private Long id;
    private String providerName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean markedUnavailable;
    private Long version;

    public AvailabilitySlot() { }

    public AvailabilitySlot(Long id, String providerName, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, boolean markedUnavailable, Long version) {
        this.id = id;
        this.providerName = providerName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.markedUnavailable = markedUnavailable;
        this.version = version;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public boolean isMarkedUnavailable() { return markedUnavailable; }
    public void setMarkedUnavailable(boolean markedUnavailable) { this.markedUnavailable = markedUnavailable; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}