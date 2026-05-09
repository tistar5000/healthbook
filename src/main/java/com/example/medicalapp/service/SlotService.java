package com.example.medicalapp.service;

import com.example.medicalapp.model.AvailabilitySlot;
import com.example.medicalapp.repository.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SlotService {

    private static final Logger log = LoggerFactory.getLogger(SlotService.class);
    private final SlotRepository slotRepository;

    public SlotService(SlotRepository slotRepository) { this.slotRepository = slotRepository; }

    public List<AvailabilitySlot> getAvailableSlots() {
        List<AvailabilitySlot> slots = slotRepository.findAllAvailableSlots();
        log.info("[SLOTS] Retrieved available slots | count={}", slots.size());
        return slots;
    }

    public Optional<AvailabilitySlot> findById(Long slotId) { return slotRepository.findById(slotId); }
}