package com.example.medicalapp.repository;

import com.example.medicalapp.model.Appointment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private final RowMapper<Appointment> appointmentRowMapper = (rs, rowNum) ->
            new Appointment(
                    rs.getLong("id"),
                    rs.getLong("slot_id"),
                    rs.getString("patient_name"),
                    rs.getString("provider_name"),
                    rs.getTimestamp("appointment_start").toLocalDateTime(),
                    rs.getTimestamp("appointment_end").toLocalDateTime(),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

    public int insertAppointment(Long slotId, String patientName, Long userId,
                                 String providerName,
                                 LocalDateTime appointmentStart,
                                 LocalDateTime appointmentEnd) {
        String sql = """
            INSERT INTO appointments
            (slot_id, patient_name, user_id, provider_name, appointment_start, appointment_end)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        return jdbcTemplate.update(sql, slotId, patientName, userId, providerName,
                appointmentStart, appointmentEnd);
    }

    public List<Appointment> findAll() {
        String sql = """
                SELECT id, slot_id, patient_name, provider_name, appointment_start, appointment_end, created_at
                FROM appointments
                ORDER BY appointment_start
                """;
        return jdbcTemplate.query(sql, appointmentRowMapper);
    }

    public List<Appointment> findByUserId(Long userId) {
        String sql = """
            SELECT id, slot_id, patient_name, provider_name,
                   appointment_start, appointment_end, created_at
            FROM appointments
            WHERE user_id = ?
              AND cancelled = FALSE
            ORDER BY appointment_start
            """;
        return jdbcTemplate.query(sql, appointmentRowMapper, userId);
    }

    public int cancelAppointment(Long appointmentId, Long userId) {
        String sql = """
            UPDATE appointments
            SET cancelled = TRUE
            WHERE id = ?
              AND user_id = ?
            """;
        return jdbcTemplate.update(sql, appointmentId, userId);
    }

    public Optional<Appointment> findBySlotId(Long slotId) {
        String sql = """
                SELECT id, slot_id, patient_name, provider_name, appointment_start, appointment_end, created_at
                FROM appointments
                WHERE slot_id = ?
                """;
        List<Appointment> results = jdbcTemplate.query(sql, appointmentRowMapper, slotId);
        return results.stream().findFirst();
    }

    public long countAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0 : count;
    }
}