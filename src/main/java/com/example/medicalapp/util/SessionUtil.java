package com.example.medicalapp.util;

import com.example.medicalapp.model.User;
import jakarta.servlet.http.HttpSession;

// session handler
// controllers don't have session keys
public class SessionUtil {

    public static final String USER_ID_KEY   = "userId";
    public static final String USER_NAME_KEY = "userName";

    private SessionUtil() {}

    public static void setUser(HttpSession session, User user) {
        session.setAttribute(USER_ID_KEY,   user.getId());
        session.setAttribute(USER_NAME_KEY, user.getFullName());
    }

    public static Long getUserId(HttpSession session) { return (Long) session.getAttribute(USER_ID_KEY); }

    public static String getUserName(HttpSession session) { return (String) session.getAttribute(USER_NAME_KEY); }

    public static boolean isLoggedIn(HttpSession session) { return session.getAttribute(USER_ID_KEY) != null; }

    public static void clear(HttpSession session) { session.invalidate(); }
}