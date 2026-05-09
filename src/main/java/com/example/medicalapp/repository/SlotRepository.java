package com.example.medicalapp.repository;

import com.example.medicalapp.model.AvailabilitySlot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class SlotRepository {

    private final JdbcTemplate jdbcTemplate;

    public SlotRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private final RowMapper<AvailabilitySlot> slotRowMapper = (rs, rowNum) ->
            new AvailabilitySlot(
                    rs.getLong("id"),
                    rs.getString("provider_name"),
                    rs.getTimestamp("start_date_time").toLocalDateTime(),
                    rs.getTimestamp("end_date_time").toLocalDateTime(),
                    rs.getBoolean("marked_unavailable"),
                    rs.getLong("version")
            );

    public List<AvailabilitySlot> findAllAvailableSlots() {
        String sql = """
                SELECT id, provider_name, start_date_time, end_date_time, marked_unavailable, version
                FROM availability_slots
                WHERE marked_unavailable = FALSE
                ORDER BY start_date_time
                """;
        return jdbcTemplate.query(sql, slotRowMapper);
    }

    public Optional<AvailabilitySlot> findById(Long id) {
        String sql = """
            SELECT id, provider_name, start_date_time, end_date_time,
                   marked_unavailable, version
            FROM availability_slots
            WHERE id = ?
              AND marked_unavailable = FALSE
            """;
        List<AvailabilitySlot> results = jdbcTemplate.query(sql, slotRowMapper, id);
        return results.stream().findFirst();
    }

    public Optional<AvailabilitySlot> findByIdForUpdate(Long slotId) {
        String sql = """
                SELECT id, provider_name, start_date_time, end_date_time, marked_unavailable, version
                FROM availability_slots
                WHERE id = ?
                FOR UPDATE
                """;
        List<AvailabilitySlot> results = jdbcTemplate.query(sql, slotRowMapper, slotId);
        return results.stream().findFirst();
    }

    public int markSlotUnavailable(Long slotId, Long expectedVersion) {
        String sql = """
                UPDATE availability_slots
                SET marked_unavailable = TRUE,
                    version = version + 1
                WHERE id = ?
                  AND version = ?
                  AND marked_unavailable = FALSE
                """;
        return jdbcTemplate.update(sql, slotId, expectedVersion);
    }

    public int resetSlotAvailability(Long slotId) {
        String sql = """
                UPDATE availability_slots
                SET marked_unavailable = FALSE
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql, slotId);
    }

    public int createSlot(String providerName, Timestamp start, Timestamp end) {
        String sql = """
                INSERT INTO availability_slots
                (provider_name, start_date_time, end_date_time, marked_unavailable, version)
                VALUES (?, ?, ?, FALSE, 0)
                """;
        return jdbcTemplate.update(sql, providerName, start, end);
    }
}