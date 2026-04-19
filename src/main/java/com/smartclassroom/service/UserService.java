package com.smartclassroom.service;

import com.smartclassroom.dao.CourseDAO;
import com.smartclassroom.dao.RoomDAO;
import com.smartclassroom.dao.UserDAO;
import com.smartclassroom.model.Course;
import com.smartclassroom.model.User;
import com.smartclassroom.view.ConsoleView;

import java.util.List;

/**
 * UserService — user management, room management, course management for Admin.
 */
public class UserService {

    private final UserDAO   userDAO   = new UserDAO();
    private final RoomDAO   roomDAO   = new RoomDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    // ── Print all users ────────────────────────────────────────
    public void printAllUsers() {
        List<User> users = userDAO.findAll();
        ConsoleView.printSection("All Users");
        System.out.printf("  %-4s | %-12s | %-11s | %-25s | %-25s | %-6s | %s%n",
                "ID","Username","Role","Full Name","Email","Active","Joined");
        ConsoleView.printLine();
        for (User u : users) {
            System.out.printf("  %-4d | %-12s | %-11s | %-25s | %-25s | %-6s | %s%n",
                    u.getUserId(), u.getUsername(), u.getRole(),
                    u.getFullName(),
                    u.getEmail() != null ? u.getEmail() : "-",
                    u.getIsActive() == 1 ? "YES" : "NO",
                    u.getCreatedAt() != null ? u.getCreatedAt().substring(0,10) : "-");
        }
    }

    // ── Print users by role (helper for menus) ─────────────────
    public void printByRole(String role) {
        List<User> list = userDAO.findByRole(role);
        for (User u : list) {
            System.out.printf("  ID:%-3d | %-25s | %s%n",
                    u.getUserId(), u.getFullName(), u.getEmail() != null ? u.getEmail() : "-");
        }
        if (list.isEmpty()) ConsoleView.info("No users found with role: " + role);
    }

    // ── Add user ───────────────────────────────────────────────
    public boolean addUser(String username, String plainPassword, String role,
                           String fullName, String email) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(AuthService.hash(plainPassword));
        u.setRole(role.toUpperCase());
        u.setFullName(fullName);
        u.setEmail(email);
        boolean ok = userDAO.insert(u);
        if (ok) ConsoleView.success("User '" + username + "' added.");
        return ok;
    }

    // ── Deactivate user ────────────────────────────────────────
    public boolean deactivateUser(int userId) {
        boolean ok = userDAO.deactivate(userId);
        if (ok) ConsoleView.success("User ID " + userId + " deactivated.");
        else    ConsoleView.error("User not found.");
        return ok;
    }

    // ── Room management ────────────────────────────────────────
    public void printRooms()                            { roomDAO.printAll(); }

    public void printFreeRooms(java.util.List<String> busyRoomNames) {
        roomDAO.printFreeRooms(busyRoomNames);
    }

    public boolean addRoom(String name, int cap, String type) {
        boolean ok = roomDAO.insert(name, cap, type.toUpperCase());
        if (ok) ConsoleView.success("Room '" + name + "' added.");
        return ok;
    }

    public boolean assignTechnicianToRoom(int roomId, int techId) {
        boolean ok = roomDAO.assignTechnician(roomId, techId);
        if (ok) ConsoleView.success("Technician assigned to room.");
        return ok;
    }

    // ── Course management ──────────────────────────────────────
    public void printAllCourses() {
        List<Course> courses = courseDAO.findAll();
        ConsoleView.printSection("All Courses");
        System.out.printf("  %-4s | %-12s | %-35s | %s%n","ID","Code","Name","Lecturer");
        ConsoleView.printLine();
        for (Course c : courses) {
            System.out.printf("  %-4d | %-12s | %-35s | %s%n",
                    c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                    c.getLecturerName() != null ? c.getLecturerName() : "Unassigned");
        }
        if (courses.isEmpty()) ConsoleView.info("No courses found.");
    }

    public boolean addCourse(String code, String name, int lecturerId) {
        boolean ok = courseDAO.insert(code, name, lecturerId);
        if (ok) ConsoleView.success("Course '" + code + " - " + name + "' added.");
        return ok;
    }

    public boolean deleteCourse(int courseId) {
        boolean ok = courseDAO.delete(courseId);
        if (ok) ConsoleView.success("Course deleted.");
        else    ConsoleView.error("Could not delete. Course may have linked data.");
        return ok;
    }

    public boolean enrollStudent(int studentId, int courseId) {
        boolean ok = courseDAO.enroll(studentId, courseId);
        if (ok) ConsoleView.success("Student enrolled.");
        return ok;
    }

    public boolean unenrollStudent(int studentId, int courseId) {
        boolean ok = courseDAO.unenroll(studentId, courseId);
        if (ok) ConsoleView.success("Student removed from course.");
        return ok;
    }

    public void printEnrollments(int courseId) { courseDAO.printEnrollments(courseId); }
}
