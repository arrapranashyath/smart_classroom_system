package com.smartclassroom.dao;

import com.smartclassroom.model.Course;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    private static final String SELECT_BASE =
        "SELECT c.*, u.full_name AS lecturer_name " +
        "FROM courses c LEFT JOIN users u ON c.lecturer_id=u.user_id ";

    // ── Get all courses ────────────────────────────────────────
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY c.course_code");
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Get courses taught by a lecturer ───────────────────────
    public List<Course> findByLecturer(int lecturerId) {
        String sql = SELECT_BASE + "WHERE c.lecturer_id=? ORDER BY c.course_code";
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Get courses a student is enrolled in ───────────────────
    public List<Course> findByStudent(int studentId) {
        String sql = SELECT_BASE +
            "JOIN enrollments e ON c.course_id=e.course_id " +
            "WHERE e.student_id=? ORDER BY c.course_code";
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Insert course ──────────────────────────────────────────
    public boolean insert(String courseCode, String courseName, int lecturerId) {
        String sql = "INSERT INTO courses (course_code, course_name, lecturer_id) VALUES(?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, courseName);
            ps.setInt(3, lecturerId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) ConsoleView.error("Course code already exists.");
            else ConsoleView.error(e.getMessage());
            return false;
        }
    }

    // ── Delete course ──────────────────────────────────────────
    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Enroll a student ───────────────────────────────────────
    public boolean enroll(int studentId, int courseId) {
        String sql = "INSERT IGNORE INTO enrollments (student_id, course_id) VALUES(?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, courseId);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Unenroll a student ─────────────────────────────────────
    public boolean unenroll(int studentId, int courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id=? AND course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── List enrolled students for a course ───────────────────
    public void printEnrollments(int courseId) {
        String sql = "SELECT u.user_id, u.full_name, u.email, e.enrolled_at " +
                     "FROM enrollments e JOIN users u ON e.student_id=u.user_id " +
                     "WHERE e.course_id=? ORDER BY u.full_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-4s | %-25s | %-25s | %s%n", "ID","Name","Email","Enrolled At");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-4d | %-25s | %-25s | %s%n",
                        rs.getInt("user_id"), rs.getString("full_name"),
                        rs.getString("email") != null ? rs.getString("email") : "-",
                        rs.getString("enrolled_at"));
            }
            if (!found) ConsoleView.info("No students enrolled in this course.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setLecturerId(rs.getInt("lecturer_id"));
        c.setLecturerName(rs.getString("lecturer_name"));
        return c;
    }
}
