package com.smartclassroom.dao;

import com.smartclassroom.model.User;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── Fetch one user by username + password (login) ─────────
    public User findByCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=? AND is_active=1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { ConsoleView.error("Login DB error: " + e.getMessage()); }
        return null;
    }

    // ── Get all users ─────────────────────────────────────────
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Get users by role ─────────────────────────────────────
    public List<User> findByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role=? AND is_active=1 ORDER BY full_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Insert new user ───────────────────────────────────────
    public boolean insert(User u) {
        String sql = "INSERT INTO users (username,password,full_name,email,role) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getRole());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) ConsoleView.error("Username already exists.");
            else ConsoleView.error("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // ── Deactivate user (soft delete) ─────────────────────────
    public boolean deactivate(int userId) {
        String sql = "UPDATE users SET is_active=0 WHERE user_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Change password ───────────────────────────────────────
    public boolean updatePassword(int userId, String oldPass, String newPass) {
        String check = "SELECT user_id FROM users WHERE user_id=? AND password=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(check)) {
            ps.setInt(1, userId); ps.setString(2, oldPass);
            if (!ps.executeQuery().next()) { ConsoleView.error("Current password incorrect."); return false; }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }

        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newPass); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Map ResultSet row to User ─────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        u.setIsActive(rs.getInt("is_active"));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
