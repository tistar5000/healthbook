package com.example.medicalapp.controller;

import com.example.medicalapp.model.Appointment;
import com.example.medicalapp.service.AppointmentService;
import com.example.medicalapp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final AppointmentService appointmentService;

    public DashboardController(AppointmentService appointmentService) { this.appointmentService = appointmentService; }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        Long   userId   = SessionUtil.getUserId(session);
        String userName = SessionUtil.getUserName(session);
        log.info("[REQUEST] GET /dashboard | userId={} | userName={}", userId, userName);

        List<Appointment> appointments = appointmentService.getAppointmentsForUser(userId);

        // Separating the first upcoming from the rest of the appt history
        Appointment upcoming = appointments.isEmpty() ? null : appointments.get(0);

        model.addAttribute("userName",     userName);
        model.addAttribute("upcoming",     upcoming);
        model.addAttribute("appointments", appointments);
        return "dashboard";
    }

    @GetMapping("/message")
    public String messagePage(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        model.addAttribute("userName", SessionUtil.getUserName(session));
        return "message";
    }

    @GetMapping("/account")
    public String accountPage(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        model.addAttribute("userName", SessionUtil.getUserName(session));
        return "account";
    }
}