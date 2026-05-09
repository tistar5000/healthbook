package com.example.medicalapp.repository;

import com.example.medicalapp.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private final RowMapper<User> userRowMapper = (rs, rowNum) ->
            new User(
                    rs.getLong("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT id, full_name, email, password_hash, created_at
                FROM users
                WHERE email = ?
                """;
        List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
        return results.stream().findFirst();
    }

    public Optional<User> findById(Long id) {
        String sql = """
                SELECT id, full_name, email, password_hash, created_at
                FROM users
                WHERE id = ?
                """;
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, email);
        return count != null && count > 0;
    }

    public int insertUser(String fullName, String email, String passwordHash) {
        String sql = """
                INSERT INTO users (full_name, email, password_hash)
                VALUES (?, ?, ?)
                """;
        return jdbcTemplate.update(sql, fullName, email, passwordHash);
    }
}