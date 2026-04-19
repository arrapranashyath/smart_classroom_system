package com.smartclassroom;

import com.smartclassroom.controller.*;
import com.smartclassroom.model.User;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

/**
 * Main — entry point for Smart Classroom Management System.
 * Phase 4: hardened with connection retry, login attempt limit,
 *          graceful shutdown, and full role routing.
 *
 * HOW TO COMPILE:
 *   Windows : compile.bat
 *   Linux   : ./compile.sh
 *
 * HOW TO RUN:
 *   Windows : java -cp ".;out;lib\mysql-connector-j-9.6.0.jar" com.smartclassroom.Main
 *   Linux   : java -cp ".:out:lib/mysql-connector-j-9.6.0.jar" com.smartclassroom.Main
 */
public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {

        printSplash();

        // ── DB connection check ────────────────────────────────
        System.out.println("  Connecting to database...");
        if (!DBConnection.isConnected() && DBConnection.getConnection() == null) {
            System.out.println();
            System.out.println("  [FATAL] Could not connect to the database.");
            System.out.println("  Steps to fix:");
            System.out.println("    1. Open util/DBConnection.java");
            System.out.println("    2. Set DB_USER and DB_PASS to your MySQL credentials");
            System.out.println("    3. Ensure MySQL server is running");
            System.out.println("    4. Ensure 'smrtclass' database exists (run schema.sql)");
            System.out.println();
            return;
        }
        ConsoleView.success("Database connected successfully.");
        System.out.println();

        LoginController loginCtrl = new LoginController();
        boolean appRunning = true;

        // ── Main application loop ──────────────────────────────
        while (appRunning) {

            // ── Login with attempt limit ───────────────────────
            User user = null;
            int attempts = 0;

            while (user == null && attempts < MAX_LOGIN_ATTEMPTS) {
                user = loginCtrl.promptLogin();

                if (user == null && loginCtrl.isExitRequested()) {
                    appRunning = false;
                    break;
                }

                if (user == null) {
                    attempts++;
                    int remaining = MAX_LOGIN_ATTEMPTS - attempts;
                    if (remaining > 0) {
                        ConsoleView.warn("Login failed. " + remaining +
                                " attempt(s) remaining.");
                    } else {
                        ConsoleView.error("Too many failed attempts. Exiting for security.");
                        appRunning = false;
                    }
                }
            }

            if (!appRunning || user == null) break;

            // ── Dashboard ──────────────────────────────────────
            loginCtrl.showDashboard(user);
            InputHelper.pressEnter();

            // ── Route to role controller ───────────────────────
            try {
                switch (user.getRole()) {
                    case "ADMIN":
                        new AdminController(user).run();
                        break;
                    case "LECTURER":
                        new LecturerController(user).run();
                        break;
                    case "CR":
                        new CRController(user).run();
                        break;
                    case "STUDENT":
                        new StudentController(user).run();
                        break;
                    case "TECHNICIAN":
                        new TechnicianController(user).run();
                        break;
                    default:
                        ConsoleView.error("Unknown role: " + user.getRole() +
                                ". Contact admin.");
                        break;
                }
            } catch (Exception e) {
                // Catch unexpected runtime errors so the app doesn't crash
                System.out.println();
                ConsoleView.error("An unexpected error occurred: " + e.getMessage());
                ConsoleView.warn("Returning to login screen...");
            }

            // After logout return to login
            System.out.println();
            ConsoleView.printLine();
            ConsoleView.info("Session ended. Returning to login...");
            ConsoleView.printLine();
            System.out.println();
        }

        // ── Shutdown ───────────────────────────────────────────
        DBConnection.closeConnection();
        System.out.println();
        System.out.println("  ╔═════════════════════════════════════════╗");
        System.out.println("  ║   Thank you for using Smart Classroom   ║");
        System.out.println("  ║   Management System. Goodbye!           ║");
        System.out.println("  ╚═════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printSplash() {
        System.out.println();
        System.out.println("  ╔═══════════════════════════════════════════════════════════╗");
        System.out.println("  ║       SMART CLASSROOM MANAGEMENT SYSTEM                   ║");
        System.out.println("  ║       Core Java  +  JDBC  +  MySQL           v2.0         ║");
        System.out.println("  ╠═══════════════════════════════════════════════════════════╣");
        System.out.println("  ║  Roles: Admin | Lecturer | CR | Student | Technician      ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
