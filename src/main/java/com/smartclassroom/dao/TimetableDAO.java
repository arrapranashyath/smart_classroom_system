package com.smartclassroom.dao;

import com.smartclassroom.model.Timetable;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    private static final String SELECT_BASE =
        "SELECT t.slot_id, t.course_id, t.room_id, t.day_of_week, t.start_time, t.end_time, " +
        "c.course_name, c.course_code, r.room_name, u.full_name AS lecturer_name " +
        "FROM timetable t " +
        "JOIN courses c ON t.course_id = c.course_id " +
        "JOIN rooms r   ON t.room_id   = r.room_id " +
        "LEFT JOIN users u ON c.lecturer_id = u.user_id ";

    public List<Timetable> findAll() {
        return query(SELECT_BASE + "ORDER BY FIELD(t.day_of_week,'MON','TUE','WED','THU','FRI','SAT'), t.start_time");
    }

    public List<Timetable> findByDay(String day) {
        String sql = SELECT_BASE + "WHERE t.day_of_week=? ORDER BY t.start_time";
        List<Timetable> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, day);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    public List<Timetable> findByLecturer(int lecturerId) {
        String sql = SELECT_BASE + "WHERE c.lecturer_id=? ORDER BY FIELD(t.day_of_week,'MON','TUE','WED','THU','FRI','SAT'), t.start_time";
        List<Timetable> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    public List<Timetable> findByDayAndLecturer(String day, int lecturerId) {
        String sql = SELECT_BASE + "WHERE t.day_of_week=? AND c.lecturer_id=? ORDER BY t.start_time";
        List<Timetable> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, day);
            ps.setInt(2, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    public List<Timetable> searchByCourseName(String keyword) {
        String sql = SELECT_BASE + "WHERE c.course_name LIKE ? OR c.course_code LIKE ?";
        List<Timetable> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    public boolean insert(int courseId, int roomId, String day, String start, String end) {
        String sql = "INSERT INTO timetable (course_id,room_id,day_of_week,start_time,end_time) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId); ps.setInt(2, roomId);
            ps.setString(3, day); ps.setString(4, start); ps.setString(5, end);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error("Error adding slot: " + e.getMessage()); return false; }
    }

    public boolean update(int slotId, String day, String start, String end) {
        String sql = "UPDATE timetable SET day_of_week=?, start_time=?, end_time=? WHERE slot_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, day); ps.setString(2, start); ps.setString(3, end); ps.setInt(4, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    public boolean delete(int slotId) {
        String sql = "DELETE FROM timetable WHERE slot_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, slotId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    private List<Timetable> query(String sql) {
        List<Timetable> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private Timetable mapRow(ResultSet rs) throws SQLException {
        Timetable t = new Timetable();
        t.setSlotId(rs.getInt("slot_id"));
        t.setCourseId(rs.getInt("course_id"));
        t.setRoomId(rs.getInt("room_id"));
        t.setDayOfWeek(rs.getString("day_of_week"));
        t.setStartTime(rs.getString("start_time"));
        t.setEndTime(rs.getString("end_time"));
        t.setCourseName(rs.getString("course_name"));
        t.setCourseCode(rs.getString("course_code"));
        t.setRoomName(rs.getString("room_name"));
        t.setLecturerName(rs.getString("lecturer_name"));
        return t;
    }
}
