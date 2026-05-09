package com.example.medicalapp.controller;

import com.example.medicalapp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "index";
    }
}