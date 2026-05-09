package com.example.medicalapp.model;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long slotId;
    private String patientName;
    private String providerName;
    private LocalDateTime appointmentStart;
    private LocalDateTime appointmentEnd;
    private LocalDateTime createdAt;

    public Appointment() { }

    public Appointment(Long id, Long slotId, String patientName, String providerName,
                       LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                       LocalDateTime createdAt) {
        this.id = id;
        this.slotId = slotId;
        this.patientName = patientName;
        this.providerName = providerName;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.createdAt = createdAt;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public LocalDateTime getAppointmentStart() { return appointmentStart; }
    public void setAppointmentStart(LocalDateTime appointmentStart) { this.appointmentStart = appointmentStart; }

    public LocalDateTime getAppointmentEnd() { return appointmentEnd; }
    public void setAppointmentEnd(LocalDateTime appointmentEnd) { this.appointmentEnd = appointmentEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}