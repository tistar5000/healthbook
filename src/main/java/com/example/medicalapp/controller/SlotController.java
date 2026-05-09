package com.example.medicalapp.controller;

import com.example.medicalapp.model.AvailabilitySlot;
import com.example.medicalapp.service.SlotService;
import com.example.medicalapp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SlotController {

    private static final Logger log = LoggerFactory.getLogger(SlotController.class);
    private final SlotService slotService;

    public SlotController(SlotService slotService) { this.slotService = slotService; }

    @GetMapping("/slots")
    public String listSlots(@RequestParam(required = false) String provider,
                            HttpSession session,
                            Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        log.info("[REQUEST] GET /slots | userId={} | provider={}",
                SessionUtil.getUserId(session), provider);

        List<AvailabilitySlot> allSlots = slotService.getAvailableSlots();

        // provider names for the drop down
        List<String> providers = allSlots.stream()
                .map(AvailabilitySlot::getProviderName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Filtering by chosen provider (if any chosen)
        List<AvailabilitySlot> filteredSlots = (provider != null && !provider.isBlank())
                ? allSlots.stream()
                .filter(s -> s.getProviderName().equals(provider))
                .collect(Collectors.toList())
                : allSlots;

        model.addAttribute("providers",       providers);
        model.addAttribute("slots",           filteredSlots);
        model.addAttribute("selectedProvider", provider != null ? provider : "");
        return "slots";
    }
}