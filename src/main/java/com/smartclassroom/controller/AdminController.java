package com.smartclassroom.controller;

import com.smartclassroom.dao.AnnouncementDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.User;
import com.smartclassroom.service.*;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

/**
 * AdminController — Phase 4: Reports menu added, input hardened.
 */
public class AdminController {

    private final User             user;
    private final UserService      userService      = new UserService();
    private final TimetableService timetableService = new TimetableService();
    private final AuthService      authService      = new AuthService();
    private final ReportService    reportService    = new ReportService();
    private final NotificationDAO  notifDAO         = new NotificationDAO();
    private final AnnouncementDAO  announcementDAO  = new AnnouncementDAO();

    public AdminController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("ADMIN MENU");
            System.out.println("  --- USER MANAGEMENT ---");
            System.out.println("  1.  View All Users");
            System.out.println("  2.  Add New User");
            System.out.println("  3.  Deactivate User");
            System.out.println("  --- COURSE MANAGEMENT ---");
            System.out.println("  4.  View All Courses");
            System.out.println("  5.  Add Course");
            System.out.println("  6.  Delete Course");
            System.out.println("  7.  Enroll Student in Course");
            System.out.println("  8.  Remove Student from Course");
            System.out.println("  9.  View Course Enrollments");
            System.out.println("  --- ROOM MANAGEMENT ---");
            System.out.println("  10. View All Rooms");
            System.out.println("  11. Add Room");
            System.out.println("  12. Assign Technician to Room");
            System.out.println("  --- TIMETABLE ---");
            System.out.println("  13. View Full Timetable");
            System.out.println("  14. Add Timetable Slot");
            System.out.println("  15. Update Timetable Slot");
            System.out.println("  16. Delete Timetable Slot");
            System.out.println("  --- ANNOUNCEMENTS & NOTIFICATIONS ---");
            System.out.println("  17. Post Announcement");
            System.out.println("  18. View All Announcements");
            System.out.println("  19. Broadcast Notification to All");
            System.out.println("  20. View My Notifications");
            System.out.println("  --- REPORTS ---");
            System.out.println("  21. Reports Dashboard");
            System.out.println("  --- ACCOUNT ---");
            System.out.println("  22. Change My Password");
            System.out.println("  0.  Logout");
            ConsoleView.printLine();

            int ch = InputHelper.getIntInRange("Choice", 0, 22);
            switch (ch) {
                case 1:  userService.printAllUsers();                               break;
                case 2:  addUserFlow();                                             break;
                case 3:  deactivateUserFlow();                                      break;
                case 4:  userService.printAllCourses();                             break;
                case 5:  addCourseFlow();                                           break;
                case 6:  deleteCourseFlow();                                        break;
                case 7:  enrollStudentFlow();                                       break;
                case 8:  unenrollStudentFlow();                                     break;
                case 9:  viewEnrollmentsFlow();                                     break;
                case 10: userService.printRooms();                                  break;
                case 11: addRoomFlow();                                             break;
                case 12: assignTechFlow();                                          break;
                case 13: timetableService.printAll();                               break;
                case 14: addSlotFlow();                                             break;
                case 15: updateSlotFlow();                                          break;
                case 16: deleteSlotFlow();                                          break;
                case 17: postAnnouncementFlow();                                    break;
                case 18: announcementDAO.findAll().forEach(a -> {
                             System.out.println("  " + a); ConsoleView.printLine(); }); break;
                case 19: broadcastFlow();                                           break;
                case 20: viewNotificationsFlow();                                   break;
                case 21: reportService.showAdminReportsMenu();                      break;
                case 22: changePasswordFlow();                                      break;
                case 0:  running = false; System.out.println("  Logged out.");     break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    // ── Flows ──────────────────────────────────────────────────

    private void addUserFlow() {
        ConsoleView.printSection("Add New User");
        String username = InputHelper.getString("Username");
        String password = InputHelper.getString("Password");
        String role     = InputHelper.getEnum("Role","ADMIN","LECTURER","STUDENT","CR","TECHNICIAN");
        String fullName = InputHelper.getString("Full Name");
        String email    = InputHelper.getOptionalString("Email");
        userService.addUser(username, password, role, fullName, email);
    }

    private void deactivateUserFlow() {
        userService.printAllUsers();
        int id = InputHelper.getInt("User ID to deactivate");
        if (InputHelper.confirm("Deactivate user ID " + id + "?")) {
            userService.deactivateUser(id);
        } else {
            ConsoleView.info("Cancelled.");
        }
    }

    private void addCourseFlow() {
        ConsoleView.printSection("Add Course");
        String code = InputHelper.getString("Course Code (e.g. CS101)");
        String name = InputHelper.getString("Course Name");
        userService.printByRole("LECTURER");
        int lecId = InputHelper.getInt("Lecturer ID");
        userService.addCourse(code, name, lecId);
    }

    private void deleteCourseFlow() {
        userService.printAllCourses();
        int id = InputHelper.getInt("Course ID to delete");
        if (InputHelper.confirm("Delete course ID " + id + "? This removes all linked data.")) {
            userService.deleteCourse(id);
        } else {
            ConsoleView.info("Cancelled.");
        }
    }

    private void enrollStudentFlow() {
        userService.printByRole("STUDENT");
        int stuId = InputHelper.getInt("Student ID");
        userService.printAllCourses();
        int crsId = InputHelper.getInt("Course ID");
        userService.enrollStudent(stuId, crsId);
    }

    private void unenrollStudentFlow() {
        userService.printByRole("STUDENT");
        int stuId = InputHelper.getInt("Student ID");
        userService.printAllCourses();
        int crsId = InputHelper.getInt("Course ID");
        if (InputHelper.confirm("Remove student from this course?")) {
            userService.unenrollStudent(stuId, crsId);
        }
    }

    private void viewEnrollmentsFlow() {
        userService.printAllCourses();
        int crsId = InputHelper.getInt("Course ID");
        ConsoleView.printSection("Enrollments");
        userService.printEnrollments(crsId);
    }

    private void addRoomFlow() {
        ConsoleView.printSection("Add Room");
        String name = InputHelper.getString("Room Name (e.g. Room 101)");
        int    cap  = InputHelper.getInt("Capacity");
        String type = InputHelper.getEnum("Room Type","CLASSROOM","LAB","SEMINAR");
        userService.addRoom(name, cap, type);
    }

    private void assignTechFlow() {
        userService.printRooms();
        int roomId = InputHelper.getInt("Room ID");
        userService.printByRole("TECHNICIAN");
        int techId = InputHelper.getInt("Technician ID");
        userService.assignTechnicianToRoom(roomId, techId);
    }

    private void addSlotFlow() {
        ConsoleView.printSection("Add Timetable Slot");
        userService.printAllCourses();
        int courseId = InputHelper.getInt("Course ID");
        userService.printRooms();
        int roomId   = InputHelper.getInt("Room ID");
        String day   = InputHelper.getEnum("Day","MON","TUE","WED","THU","FRI","SAT");
        String start = InputHelper.getString("Start Time (HH:MM)");
        String end   = InputHelper.getString("End Time   (HH:MM)");
        timetableService.addSlot(courseId, roomId, day, start + ":00", end + ":00");
    }

    private void updateSlotFlow() {
        timetableService.printAll();
        int slotId   = InputHelper.getInt("Slot ID to update");
        String day   = InputHelper.getEnum("New Day","MON","TUE","WED","THU","FRI","SAT");
        String start = InputHelper.getString("New Start (HH:MM)");
        String end   = InputHelper.getString("New End   (HH:MM)");
        timetableService.updateSlot(slotId, day, start + ":00", end + ":00");
    }

    private void deleteSlotFlow() {
        timetableService.printAll();
        int slotId = InputHelper.getInt("Slot ID to delete");
        if (InputHelper.confirm("Delete slot ID " + slotId + "?")) {
            timetableService.deleteSlot(slotId);
        }
    }

    private void postAnnouncementFlow() {
        ConsoleView.printSection("Post Announcement");
        String title   = InputHelper.getString("Title");
        String content = InputHelper.getString("Content");
        String target  = InputHelper.getEnum("Target Role","ALL","STUDENT","LECTURER","CR","TECHNICIAN");
        if (announcementDAO.insert(user.getUserId(), title, content, target)) {
            ConsoleView.success("Announcement posted.");
            if ("ALL".equals(target)) {
                notifDAO.broadcastAll("New Announcement: " + title, content);
            } else {
                notifDAO.broadcastToRole(target, "New Announcement: " + title, content);
            }
        }
    }

    private void broadcastFlow() {
        ConsoleView.printSection("Broadcast to All Users");
        String title   = InputHelper.getString("Notification Title");
        String message = InputHelper.getString("Message");
        if (InputHelper.confirm("Send this notification to ALL users?")) {
            notifDAO.broadcastAll(title, message);
            ConsoleView.success("Broadcast sent to all users.");
        }
    }

    private void viewNotificationsFlow() {
        ConsoleView.printSection("My Notifications");
        var list = notifDAO.findByUser(user.getUserId());
        if (list.isEmpty()) { ConsoleView.info("No notifications."); return; }
        list.forEach(n -> System.out.println("  " + n));
        notifDAO.markAllRead(user.getUserId());
    }

    private void changePasswordFlow() {
        ConsoleView.printSection("Change Password");
        String oldP = InputHelper.getString("Current Password");
        String newP = InputHelper.getString("New Password");
        String cnf  = InputHelper.getString("Confirm New Password");
        if (!newP.equals(cnf)) { ConsoleView.error("Passwords do not match."); return; }
        authService.changePassword(user.getUserId(), oldP, newP);
    }
}
