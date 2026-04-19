package com.smartclassroom.dao;

import com.smartclassroom.model.Issue;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    private static final String SELECT_BASE =
        "SELECT i.*, r.room_name, " +
        "rep.full_name AS reporter_name, " +
        "COALESCE(asgn.full_name,'Unassigned') AS assignee_name " +
        "FROM issues i " +
        "LEFT JOIN rooms r      ON i.room_id     = r.room_id " +
        "JOIN  users rep        ON i.reported_by = rep.user_id " +
        "LEFT JOIN users asgn   ON i.assigned_to = asgn.user_id ";

    // ── Report a new issue (auto-assigns technician from room_technicians) ──
    public boolean insert(int reportedBy, int roomId, String title, String description, String priority) {
        int techId = getTechnicianForRoom(roomId);
        String sql = "INSERT INTO issues (title,description,reported_by,assigned_to,room_id,priority) " +
                     "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, reportedBy);
            if (techId > 0) ps.setInt(4, techId); else ps.setNull(4, Types.INTEGER);
            if (roomId > 0) ps.setInt(5, roomId); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, priority);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { ConsoleView.error("Error reporting issue: " + e.getMessage()); return false; }
    }

    // ── Lookup technician for a room via room_technicians ─────
    public int getTechnicianForRoom(int roomId) {
        String sql = "SELECT technician_id FROM room_technicians WHERE room_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("technician_id");
        } catch (SQLException e) { /* silent */ }
        return -1;
    }

    public List<Issue> findAll() {
        return query(SELECT_BASE +
            "ORDER BY FIELD(i.status,'OPEN','IN_PROGRESS','RESOLVED','CLOSED'), i.created_at DESC");
    }

    public List<Issue> findByTechnician(int techId) {
        String sql = SELECT_BASE + "WHERE i.assigned_to=? ORDER BY FIELD(i.status,'OPEN','IN_PROGRESS','RESOLVED','CLOSED')";
        return queryWithInt(sql, techId);
    }

    public List<Issue> searchByRoom(String roomKeyword) {
        String sql = SELECT_BASE + "WHERE r.room_name LIKE ?";
        List<Issue> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + roomKeyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    public boolean updateStatus(int issueId, String newStatus) {
        String sql = "RESOLVED".equals(newStatus)
            ? "UPDATE issues SET status=?, resolved_at=NOW() WHERE issue_id=?"
            : "UPDATE issues SET status=? WHERE issue_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus); ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Get reporter's user_id for an issue (for notification) ─
    public int getReporterId(int issueId) {
        String sql = "SELECT reported_by FROM issues WHERE issue_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* silent */ }
        return -1;
    }

    private List<Issue> query(String sql) {
        List<Issue> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private List<Issue> queryWithInt(String sql, int id) {
        List<Issue> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private Issue mapRow(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setIssueId(rs.getInt("issue_id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setReportedBy(rs.getInt("reported_by"));
        i.setAssignedTo(rs.getInt("assigned_to"));
        i.setRoomId(rs.getInt("room_id"));
        i.setStatus(rs.getString("status"));
        i.setPriority(rs.getString("priority"));
        i.setCreatedAt(rs.getString("created_at"));
        i.setResolvedAt(rs.getString("resolved_at"));
        i.setRoomName(rs.getString("room_name"));
        i.setReporterName(rs.getString("reporter_name"));
        i.setAssigneeName(rs.getString("assignee_name"));
        return i;
    }
}
