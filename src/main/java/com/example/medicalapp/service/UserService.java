package com.example.medicalapp.service;

import com.example.medicalapp.model.User;
import com.example.medicalapp.repository.UserRepository;
import com.example.medicalapp.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // new user registration
    // checks if email already exists
    public void register(String fullName, String email, String plainPassword) {
        if (userRepository.existsByEmail(email)) {
            log.warn("[REGISTER FAILED] Email already in use | email={}", email);
            throw new IllegalArgumentException("An account with that email already exists.");
        }
        String hash = PasswordUtil.hash(plainPassword);
        userRepository.insertUser(fullName, email, hash);
        log.info("[REGISTER] New user registered | email={}", email);
    }

    // existing user login
    public Optional<User> login(String email, String plainPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("[LOGIN FAILED] Email not found | email={}", email);
            return Optional.empty();
        }
        if (!PasswordUtil.matches(plainPassword, user.get().getPasswordHash())) {
            log.warn("[LOGIN FAILED] Wrong password | email={}", email);
            return Optional.empty();
        }
        log.info("[LOGIN] User authenticated | email={} | userId={}", email, user.get().getId());
        return user;
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
}