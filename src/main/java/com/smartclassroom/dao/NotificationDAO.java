package com.smartclassroom.dao;

import com.smartclassroom.model.Notification;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // ── Send to one user ──────────────────────────────────────
    public boolean send(int userId, String title, String message) {
        String sql = "INSERT INTO notifications (user_id, title, message) VALUES(?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Broadcast to all users of a given role ─────────────────
    public void broadcastToRole(String role, String title, String message) {
        String userSql = "SELECT user_id FROM users WHERE role=? AND is_active=1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(userSql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) send(rs.getInt("user_id"), title, message);
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── Broadcast to everyone ─────────────────────────────────
    public void broadcastAll(String title, String message) {
        String userSql = "SELECT user_id FROM users WHERE is_active=1";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(userSql);
            while (rs.next()) send(rs.getInt("user_id"), title, message);
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── Fetch notifications for a user ─────────────────────────
    public List<Notification> findByUser(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC LIMIT 40";
        List<Notification> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Count unread ───────────────────────────────────────────
    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* silent */ }
        return 0;
    }

    // ── Mark all read ──────────────────────────────────────────
    public void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read=1 WHERE user_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { /* silent */ }
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setIsRead(rs.getInt("is_read"));
        n.setCreatedAt(rs.getString("created_at"));
        return n;
    }
}
