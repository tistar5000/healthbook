package com.example.medicalapp.service;

import com.example.medicalapp.exception.SlotNotFoundException;
import com.example.medicalapp.exception.SlotUnavailableException;
import com.example.medicalapp.model.Appointment;
import com.example.medicalapp.model.AvailabilitySlot;
import com.example.medicalapp.monitoring.BookingMetrics;
import com.example.medicalapp.repository.AppointmentRepository;
import com.example.medicalapp.repository.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// slot booking for appointment for the patient
// double booking prevention measures: row lock & optimistic version check
@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final SlotRepository        slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final BookingMetrics        metrics;

    public AppointmentService(SlotRepository slotRepository,
                              AppointmentRepository appointmentRepository,
                              BookingMetrics metrics) {
        this.slotRepository        = slotRepository;
        this.appointmentRepository = appointmentRepository;
        this.metrics               = metrics;
    }

    public List<Appointment> getAppointmentsForUser(Long userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public boolean cancelAppointment(Long appointmentId, Long userId) {
        int rows = appointmentRepository.cancelAppointment(appointmentId, userId);
        if (rows > 0) {
            log.info("[CANCEL] appointmentId={} | userId={}", appointmentId, userId);
            return true;
        }
        log.warn("[CANCEL FAILED] appointmentId={} | userId={} | not found or not owner",
                appointmentId, userId);
        return false;
    }

    // to prevent any phantom readings withing same transaction
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public Appointment bookAppointment(Long slotId, String patientName, Long userId) {
        long start = System.currentTimeMillis();
        log.info("[BOOKING ATTEMPT] patientName={} | slotId={}", patientName, slotId);

        try {
            // Locking the row
            AvailabilitySlot slot = slotRepository.findByIdForUpdate(slotId)
                    .orElseThrow(() -> {
                        log.warn("[BOOKING REJECTED] slotId={} not found", slotId);
                        return new SlotNotFoundException("Slot not found: " + slotId);
                    });

            // Verifying availability
            if (slot.isMarkedUnavailable()) {
                log.warn("[BOOKING REJECTED] slotId={} already unavailable | patientName={} | concurrency conflict",
                        slotId, patientName);
                metrics.recordFailure();
                throw new SlotUnavailableException("Slot " + slotId + " is already booked.");
            }

            // Adding appt record
            appointmentRepository.insertAppointment(
                    slotId, patientName, userId, slot.getProviderName(),
                    slot.getStartDateTime(), slot.getEndDateTime()
            );

            // Slot marked as unavailable
            // + optimistic version guard
            int updated = slotRepository.markSlotUnavailable(slotId, slot.getVersion());
            if (updated == 0) {
                log.warn("[OPTIMISTIC LOCK] Version mismatch on slotId={} | rowsUpdated=0 | concurrent modification detected",
                        slotId);
                metrics.recordFailure();
                throw new SlotUnavailableException("Slot " + slotId + " was modified concurrently. Please try again.");
            }

            long latency = System.currentTimeMillis() - start;
            log.info("[BOOKING CONFIRMED] slotId={} | patientName={} | provider={} | latencyMs={}",
                    slotId, patientName, slot.getProviderName(), latency);
            metrics.recordSuccess(latency);

            // Created appt is returned for controller
            return appointmentRepository.findBySlotId(slotId)
                    .orElseThrow(() -> new RuntimeException("Appointment insert failed silently"));

        } catch (SlotUnavailableException | SlotNotFoundException e) {
            // leaving for controller
            throw e;
        } catch (Exception e) {
            log.error("[BOOKING ERROR] Unexpected exception | slotId={} | patientName={} | error={}",
                    slotId, patientName, e.getMessage(), e);
            metrics.recordFailure();
            throw e;
        }
    }

    public List<Appointment> getAllAppointments() { return appointmentRepository.findAll(); }
}