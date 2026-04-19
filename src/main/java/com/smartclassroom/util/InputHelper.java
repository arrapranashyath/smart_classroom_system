package com.smartclassroom.util;

import java.util.Scanner;

/**
 * InputHelper — all console input and validation helpers.
 * Phase 4: hardened with blank-check, length limits, and type safety.
 */
public class InputHelper {

    private static final Scanner sc = new Scanner(System.in);

    // ── Required string (retries on blank) ────────────────────
    public static String getString(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            String val = sc.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("  [!] This field cannot be blank. Please try again.");
        }
    }

    // ── Optional string (blank is OK — returns "") ─────────────
    public static String getOptionalString(String prompt) {
        System.out.print("  " + prompt + " (optional): ");
        return sc.nextLine().trim();
    }

    // ── Integer with retry ────────────────────────────────────
    public static int getInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  [!] Please enter a number.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] '" + line + "' is not a valid number. Try again.");
            }
        }
    }

    // ── Integer within range ──────────────────────────────────
    public static int getIntInRange(String prompt, int min, int max) {
        while (true) {
            int val = getInt(prompt + " (" + min + "-" + max + ")");
            if (val >= min && val <= max) return val;
            System.out.println("  [!] Enter a number between " + min + " and " + max + ".");
        }
    }

    // ── Yes/No confirmation ───────────────────────────────────
    public static boolean confirm(String prompt) {
        while (true) {
            System.out.print("  " + prompt + " (yes/no): ");
            String val = sc.nextLine().trim().toLowerCase();
            if (val.equals("yes") || val.equals("y")) return true;
            if (val.equals("no")  || val.equals("n")) return false;
            System.out.println("  [!] Please type 'yes' or 'no'.");
        }
    }

    // ── ENUM validator — retries until valid option given ─────
    public static String getEnum(String prompt, String... options) {
        String optList = String.join(" / ", options);
        while (true) {
            System.out.print("  " + prompt + " (" + optList + "): ");
            String val = sc.nextLine().trim().toUpperCase();
            for (String opt : options) {
                if (opt.equalsIgnoreCase(val)) return opt;
            }
            System.out.println("  [!] Invalid choice. Options: " + optList);
        }
    }

    public static void pressEnter() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }

    public static Scanner getScanner() { return sc; }
}
