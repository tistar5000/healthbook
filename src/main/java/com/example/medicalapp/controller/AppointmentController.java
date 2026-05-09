package com.example.medicalapp.controller;

import com.example.medicalapp.exception.SlotNotFoundException;
import com.example.medicalapp.exception.SlotUnavailableException;
import com.example.medicalapp.model.Appointment;
import com.example.medicalapp.model.AvailabilitySlot;
import com.example.medicalapp.model.NotificationRequest;
import com.example.medicalapp.service.AppointmentService;
import com.example.medicalapp.service.NotificationService;
import com.example.medicalapp.service.SlotService;
import com.example.medicalapp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService  appointmentService;
    private final NotificationService notificationService;
    private final SlotService         slotService;

    public AppointmentController(AppointmentService appointmentService,
                                 NotificationService notificationService,
                                 SlotService slotService) {
        this.appointmentService  = appointmentService;
        this.notificationService = notificationService;
        this.slotService         = slotService;
    }

    // REVIEW

    @GetMapping("/book/review")
    public String showReview(@RequestParam Long slotId,
                             HttpSession session,
                             Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        Optional<AvailabilitySlot> slot = slotService.findById(slotId);
        if (slot.isEmpty()) {
            model.addAttribute("error", "Slot not found. Please choose from the list below.");
            model.addAttribute("slots", slotService.getAvailableSlots());
            return "slots";
        }

        model.addAttribute("slot",     slot.get());
        model.addAttribute("userName", SessionUtil.getUserName(session));
        return "book-review";
    }

    // CONFIRM + COMMIT

    @PostMapping("/book/confirm")
    public String confirmBooking(@RequestParam Long   slotId,
                                 @RequestParam String patientName,
                                 HttpSession session,
                                 Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        Long userId = SessionUtil.getUserId(session);
        log.info("[REQUEST] POST /book/confirm | slotId={} | patientName={} | userId={}",
                slotId, patientName, userId);

        try {
            Appointment appt = appointmentService.bookAppointment(slotId, patientName, userId);

            NotificationRequest notif = new NotificationRequest(
                    patientName,
                    appt.getProviderName(),
                    appt.getAppointmentStart().toString(),
                    patientName.toLowerCase().replace(" ", ".") + "@example.com"
            );
            notificationService.sendConfirmationAsync(notif);

            model.addAttribute("appointment", appt);
            return "confirmation";

        } catch (SlotUnavailableException e) {
            log.warn("[BOOKING REJECTED] slotId={} | reason={}", slotId, e.getMessage());
            model.addAttribute("error", "That slot was just taken. Please choose another.");
            model.addAttribute("slots", slotService.getAvailableSlots());
            return "slots";

        } catch (SlotNotFoundException e) {
            log.warn("[BOOKING REJECTED] slotId={} | reason={}", slotId, e.getMessage());
            model.addAttribute("error", "Slot not found. Please choose from the list below.");
            model.addAttribute("slots", slotService.getAvailableSlots());
            return "slots";

        } catch (Exception e) {
            log.error("[BOOKING ERROR] slotId={} | error={}", slotId, e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            model.addAttribute("slots", slotService.getAvailableSlots());
            return "slots";
        }
    }

    // APPT LIST

    @GetMapping("/appointments")
    public String listAppointments(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        List<Appointment> appointments =
                appointmentService.getAppointmentsForUser(SessionUtil.getUserId(session));
        model.addAttribute("appointments", appointments);
        return "appointments";
    }

    // CANCEL

    @PostMapping("/appointments/cancel")
    public String cancelAppointment(@RequestParam Long appointmentId,
                                    HttpSession session,
                                    Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        appointmentService.cancelAppointment(appointmentId, SessionUtil.getUserId(session));
        return "redirect:/dashboard";
    }
}