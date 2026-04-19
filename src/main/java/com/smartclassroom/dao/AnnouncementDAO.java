package com.smartclassroom.dao;

import com.smartclassroom.model.Announcement;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    private static final String SELECT_BASE =
        "SELECT a.*, u.full_name AS poster_name " +
        "FROM announcements a JOIN users u ON a.posted_by=u.user_id ";

    // ── Post an announcement ───────────────────────────────────
    public boolean insert(int postedBy, String title, String content, String targetRole) {
        String sql = "INSERT INTO announcements (title, content, posted_by, target_role) VALUES(?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, postedBy);
            ps.setString(4, targetRole);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── All announcements ──────────────────────────────────────
    public List<Announcement> findAll() {
        return query(SELECT_BASE + "ORDER BY a.created_at DESC");
    }

    // ── Announcements visible to a given role (ALL + their own role) ──
    public List<Announcement> findForRole(String role) {
        String sql = SELECT_BASE + "WHERE a.target_role='ALL' OR a.target_role=? ORDER BY a.created_at DESC";
        List<Announcement> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private List<Announcement> query(String sql) {
        List<Announcement> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private Announcement mapRow(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setAnnouncementId(rs.getInt("announcement_id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setPostedBy(rs.getInt("posted_by"));
        a.setTargetRole(rs.getString("target_role"));
        a.setCreatedAt(rs.getString("created_at"));
        a.setPosterName(rs.getString("poster_name"));
        return a;
    }
}
