package com.smartclassroom.dao;

import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;

public class RoomDAO {

    // ── Print all rooms ────────────────────────────────────────
    public void printAll() {
        String sql =
            "SELECT r.room_id, r.room_name, r.capacity, r.room_type, " +
            "COALESCE(u.full_name,'Unassigned') AS technician " +
            "FROM rooms r LEFT JOIN room_technicians rt ON r.room_id=rt.room_id " +
            "LEFT JOIN users u ON rt.technician_id=u.user_id " +
            "ORDER BY r.room_name";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-4s | %-15s | %-8s | %-10s | %s%n",
                    "ID","Room","Capacity","Type","Technician");
            ConsoleView.printLine();
            while (rs.next()) {
                System.out.printf("  %-4d | %-15s | %-8d | %-10s | %s%n",
                        rs.getInt("room_id"), rs.getString("room_name"),
                        rs.getInt("capacity"), rs.getString("room_type"),
                        rs.getString("technician"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── Add a room ─────────────────────────────────────────────
    public boolean insert(String roomName, int capacity, String roomType) {
        String sql = "INSERT INTO rooms (room_name, capacity, room_type) VALUES(?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, roomName); ps.setInt(2, capacity); ps.setString(3, roomType);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Get last inserted room_id ──────────────────────────────
    public int getLastInsertedId() {
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* silent */ }
        return -1;
    }

    // ── Assign or update technician for a room ─────────────────
    public boolean assignTechnician(int roomId, int technicianId) {
        String sql = "INSERT INTO room_technicians (room_id, technician_id) VALUES(?,?) " +
                     "ON DUPLICATE KEY UPDATE technician_id=VALUES(technician_id)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId); ps.setInt(2, technicianId);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Remove technician from a room ──────────────────────────
    public boolean removeTechnician(int roomId) {
        String sql = "DELETE FROM room_technicians WHERE room_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Print rooms NOT in today's busy list (free rooms) ─────
    public void printFreeRooms(java.util.List<String> busyRoomNames) {
        String sql =
            "SELECT r.room_id, r.room_name, r.capacity, r.room_type, " +
            "COALESCE(u.full_name,'Unassigned') AS technician " +
            "FROM rooms r LEFT JOIN room_technicians rt ON r.room_id=rt.room_id " +
            "LEFT JOIN users u ON rt.technician_id=u.user_id " +
            "ORDER BY r.room_name";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            boolean anyFree = false;
            while (rs.next()) {
                String name = rs.getString("room_name");
                if (!busyRoomNames.contains(name)) {
                    if (!anyFree) {
                        System.out.printf("  %-15s | %-8s | %-10s | %s%n",
                                "Room", "Capacity", "Type", "Technician");
                        ConsoleView.printLine();
                    }
                    anyFree = true;
                    System.out.printf("  %-15s | %-8d | %-10s | %s%n",
                            name, rs.getInt("capacity"),
                            rs.getString("room_type"), rs.getString("technician"));
                }
            }
            if (!anyFree) ConsoleView.info("All rooms are occupied today.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }
}
