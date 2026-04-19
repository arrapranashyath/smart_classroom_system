package com.smartclassroom.dao;

import com.smartclassroom.model.Attendance;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    private static final String SELECT_BASE =
        "SELECT a.*, u.full_name AS student_name, c.course_name, " +
        "m.full_name AS marked_by_name " +
        "FROM attendance a " +
        "JOIN users u    ON a.student_id = u.user_id " +
        "JOIN courses c  ON a.course_id  = c.course_id " +
        "LEFT JOIN users m ON a.marked_by = m.user_id ";

    // ── Upsert attendance for one student ─────────────────────
    public boolean upsert(int studentId, int courseId, String date, String status, int markedBy) {
        String sql = "INSERT INTO attendance (student_id, course_id, date, status, marked_by) " +
                     "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE status=VALUES(status), marked_by=VALUES(marked_by)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, courseId);
            ps.setString(3, date);   ps.setString(4, status); ps.setInt(5, markedBy);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── All attendance for a student ──────────────────────────
    public List<Attendance> findByStudent(int studentId) {
        String sql = SELECT_BASE + "WHERE a.student_id=? ORDER BY c.course_name, a.date DESC";
        return queryWithInt(sql, studentId);
    }

    // ── All attendance for a course on a date ─────────────────
    public List<Attendance> findByCourseAndDate(int courseId, String date) {
        String sql = SELECT_BASE + "WHERE a.course_id=? AND a.date=? ORDER BY u.full_name";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId); ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── All attendance for a course ───────────────────────────
    public List<Attendance> findByCourse(int courseId) {
        String sql = SELECT_BASE + "WHERE a.course_id=? ORDER BY a.date DESC, u.full_name";
        return queryWithInt(sql, courseId);
    }

    // ── Search by student name ────────────────────────────────
    public List<Attendance> searchByStudentName(String keyword) {
        String sql = SELECT_BASE + "WHERE u.full_name LIKE ?";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Analytics: per-course summary for a student ───────────
    public void printAnalytics(int studentId) {
        String sql =
            "SELECT c.course_name, COUNT(*) AS total, " +
            "SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended " +
            "FROM attendance a JOIN courses c ON a.course_id=c.course_id " +
            "WHERE a.student_id=? GROUP BY c.course_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-30s | %-6s | %-8s | %-26s | %s%n",
                    "Course","Total","Present","Progress","Status");
            ConsoleView.printLine();
            while (rs.next()) {
                int total    = rs.getInt("total");
                int attended = rs.getInt("attended");
                double pct   = total > 0 ? attended * 100.0 / total : 0;
                System.out.printf("  %-30s | %-6d | %-8d | %-26s | %s%n",
                        rs.getString("course_name"), total, attended,
                        ConsoleView.attendanceBar(pct),
                        ConsoleView.attendanceStatus(pct));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    private List<Attendance> queryWithInt(String sql, int id) {
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setDate(rs.getString("date"));
        a.setStatus(rs.getString("status"));
        a.setMarkedBy(rs.getInt("marked_by"));
        a.setStudentName(rs.getString("student_name"));
        a.setCourseName(rs.getString("course_name"));
        a.setMarkedByName(rs.getString("marked_by_name"));
        return a;
    }
}
