package com.smartclassroom.controller;

import com.smartclassroom.dao.IssueDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.Timetable;
import com.smartclassroom.model.User;
import com.smartclassroom.service.AuthService;
import com.smartclassroom.service.TimetableService;
import com.smartclassroom.service.UserService;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

import java.util.List;
import java.util.stream.Collectors;

public class TechnicianController {

    private final User             user;
    private final IssueDAO         issueDAO         = new IssueDAO();
    private final NotificationDAO  notifDAO         = new NotificationDAO();
    private final TimetableService timetableService = new TimetableService();
    private final UserService      userService      = new UserService();
    private final AuthService      authService      = new AuthService();

    public TechnicianController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("TECHNICIAN MENU");
            System.out.println("  --- MY ISSUES ---");
            System.out.println("  1.  View My Assigned Issues");
            System.out.println("  2.  Update Issue Status");
            System.out.println("  --- ROOMS & AVAILABILITY ---");
            System.out.println("  3.  View All Rooms");
            System.out.println("  4.  View Today's Timetable (Room Availability)");
            System.out.println("  --- NOTIFICATIONS & ACCOUNT ---");
            System.out.println("  5.  View Notifications");
            System.out.println("  6.  Change Password");
            System.out.println("  0.  Logout");
            ConsoleView.printLine();

            int ch = InputHelper.getIntInRange("Choice", 0, 6);
            switch (ch) {
                case 1:  viewMyIssues();          break;
                case 2:  updateStatusFlow();      break;
                case 3:  userService.printRooms(); break;
                case 4:  printRoomAvailability(); break;
                case 5:  viewNotificationsFlow(); break;
                case 6:  changePasswordFlow();    break;
                case 0:  running = false; System.out.println("  Logged out."); break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    // ── Room availability: today's schedule + free/busy status ─
    private void printRoomAvailability() {
        ConsoleView.printSection("Room Availability — Today");
        List<Timetable> todaySlots = timetableService.getTodaySlots();

        // Collect rooms that have classes today
        List<String> busyRooms = todaySlots.stream()
                .map(Timetable::getRoomName)
                .distinct()
                .collect(Collectors.toList());

        if (todaySlots.isEmpty()) {
            ConsoleView.info("No classes today — all rooms are free.");
        } else {
            System.out.printf("  %-10s | %-15s | %-35s | %s-%s%n",
                    "Status", "Room", "Course", "Start", "End");
            ConsoleView.printLine();
            for (Timetable t : todaySlots) {
                System.out.printf("  %-10s | %-15s | %-35s | %s-%s%n",
                        "[BUSY]",
                        t.getRoomName() != null ? t.getRoomName() : "Room#" + t.getRoomId(),
                        (t.getCourseCode() != null ? t.getCourseCode() + " " : "") +
                        (t.getCourseName() != null ? t.getCourseName() : ""),
                        t.getStartTime() != null ? t.getStartTime().substring(0, 5) : "?",
                        t.getEndTime()   != null ? t.getEndTime().substring(0, 5)   : "?");
            }
        }

        // Show free rooms (those not in today's schedule)
        System.out.println();
        ConsoleView.info("Rooms with NO classes today (free for maintenance):");
        userService.printFreeRooms(busyRooms);
    }

    private void viewMyIssues() {
        ConsoleView.printSection("My Assigned Issues");
        var list = issueDAO.findByTechnician(user.getUserId());
        if (list.isEmpty()) { ConsoleView.info("No issues assigned to you."); return; }
        for (var i : list) {
            System.out.println("  " + i);
            if (i.getDescription() != null && !i.getDescription().isEmpty())
                System.out.println("       Desc: " + i.getDescription());
            ConsoleView.printLine();
        }
    }

    private void updateStatusFlow() {
        viewMyIssues();
        int issueId = InputHelper.getInt("Issue ID to update");
        var list = issueDAO.findByTechnician(user.getUserId());
        boolean valid = list.stream().anyMatch(i -> i.getIssueId() == issueId);
        if (!valid) {
            ConsoleView.error("Issue #" + issueId + " is not assigned to you.");
            return;
        }
        String status = InputHelper.getEnum("New Status", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");
        if (issueDAO.updateStatus(issueId, status)) {
            ConsoleView.success("Issue #" + issueId + " updated to " + status + ".");
            int reporterId = issueDAO.getReporterId(issueId);
            if (reporterId > 0) {
                notifDAO.send(reporterId, "Issue Update",
                        "Your issue #" + issueId + " status is now: " + status);
                ConsoleView.info("Reporter has been notified.");
            }
        } else {
            ConsoleView.error("Update failed. Issue not found.");
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
