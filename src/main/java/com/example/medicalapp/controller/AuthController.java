package com.example.medicalapp.controller;

import com.example.medicalapp.model.User;
import com.example.medicalapp.service.UserService;
import com.example.medicalapp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    // LOGIN

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) return "redirect:/dashboard";
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Optional<User> user = userService.login(email, password);
        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "login";
        }
        SessionUtil.setUser(session, user.get());
        return "redirect:/dashboard";
    }

    // REGISTRATION

    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) return "redirect:/dashboard";
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String fullName,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  HttpSession session,
                                  Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "register";
        }
        try {
            userService.register(fullName, email, password);
            // Auto-login after successful registration
            Optional<User> user = userService.login(email, password);
            user.ifPresent(u -> SessionUtil.setUser(session, u));
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // LOGOUT

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("[LOGOUT] userId={}", SessionUtil.getUserId(session));
        SessionUtil.clear(session);
        return "redirect:/";
    }
}