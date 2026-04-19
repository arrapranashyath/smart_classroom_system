package com.smartclassroom.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — singleton JDBC connection to MySQL.
 * Phase 4: hardened with reconnect logic and clear error messages.
 * Update DB_URL, DB_USER, DB_PASS before running.
 */
public class DBConnection {

    private static final String DB_URL  =
        "jdbc:mysql://localhost:3306/smrtclass" +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Pranu1975"; // <-- change this

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] MySQL JDBC Driver not found.");
            System.out.println("        Make sure mysql-connector-j-8.0.33.jar is in lib/ folder.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Cannot connect to database.");
            System.out.println("        Message : " + e.getMessage());
            System.out.println("        Check   : DB_URL, DB_USER, DB_PASS in DBConnection.java");
            System.out.println("        Ensure  : MySQL server is running on localhost:3306");
        }
        return connection;
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("  Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Could not close connection: " + e.getMessage());
        }
    }
}
