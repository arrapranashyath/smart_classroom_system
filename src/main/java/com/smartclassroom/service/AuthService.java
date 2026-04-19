package com.smartclassroom.service;

import com.smartclassroom.dao.UserDAO;
import com.smartclassroom.model.User;
import com.smartclassroom.view.ConsoleView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AuthService — login, logout, password hashing and change.
 * Passwords are stored as SHA-256 hex strings (same as MySQL SHA2(p,256)).
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    // ── Hash a plain-text password ─────────────────────────────
    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ── Authenticate user ──────────────────────────────────────
    public User login(String username, String password) {
        User user = userDAO.findByCredentials(username, hash(password));
        if (user == null) {
            ConsoleView.error("Invalid username or password.");
        }
        return user;
    }

    // ── Change password ────────────────────────────────────────
    public boolean changePassword(int userId, String oldPass, String newPass) {
        boolean ok = userDAO.updatePassword(userId, hash(oldPass), hash(newPass));
        if (ok) ConsoleView.success("Password changed successfully.");
        return ok;
    }
}
