package com.example.medicalapp.model;

public class NotificationRequest {
    private String patientName;
    private String providerName;
    private String appointmentDateTime;
    private String contactEmail;

    public NotificationRequest() { }

    public NotificationRequest(String patientName, String providerName,
                               String appointmentDateTime, String contactEmail) {
        this.patientName = patientName;
        this.providerName = providerName;
        this.appointmentDateTime = appointmentDateTime;
        this.contactEmail = contactEmail;
    }

    // getters, setters
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(String appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
}