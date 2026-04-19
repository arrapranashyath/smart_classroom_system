-- ============================================================
--  Smart Classroom Management System — FINAL Schema
--  Run this entire file in MySQL to set up the database.
-- ============================================================

CREATE DATABASE IF NOT EXISTS smrtclass;
USE smrtclass;

-- Drop in reverse FK order so re-runs are clean
DROP TABLE IF EXISTS quiz_submissions;
DROP TABLE IF EXISTS quiz_questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS timetable;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS room_technicians;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

-- -------------------------------------------------------
-- 1. USERS
-- -------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(64)  NOT NULL,          -- SHA-256 hex (64 chars)
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100),
    role        ENUM('ADMIN','LECTURER','STUDENT','CR','TECHNICIAN') NOT NULL,
    is_active   TINYINT(1) DEFAULT 1,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- 2. ROOMS
-- -------------------------------------------------------
CREATE TABLE rooms (
    room_id     INT AUTO_INCREMENT PRIMARY KEY,
    room_name   VARCHAR(50)  NOT NULL UNIQUE,
    capacity    INT DEFAULT 40,
    room_type   ENUM('CLASSROOM','LAB','SEMINAR') DEFAULT 'CLASSROOM'
);

-- -------------------------------------------------------
-- 3. ROOM → TECHNICIAN MAPPING
-- -------------------------------------------------------
CREATE TABLE room_technicians (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    room_id       INT NOT NULL,
    technician_id INT NOT NULL,
    FOREIGN KEY (room_id)       REFERENCES rooms(room_id)  ON DELETE CASCADE,
    FOREIGN KEY (technician_id) REFERENCES users(user_id)  ON DELETE CASCADE,
    UNIQUE KEY uq_room_tech (room_id)
);

-- -------------------------------------------------------
-- 4. COURSES
-- -------------------------------------------------------
CREATE TABLE courses (
    course_id   INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20)  NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    lecturer_id INT,
    FOREIGN KEY (lecturer_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- -------------------------------------------------------
-- 5. ENROLLMENTS
-- -------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    enrolled_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id)   ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY uq_enrollment (student_id, course_id)
);

-- -------------------------------------------------------
-- 6. TIMETABLE
-- -------------------------------------------------------
CREATE TABLE timetable (
    slot_id     INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    room_id     INT NOT NULL,
    day_of_week ENUM('MON','TUE','WED','THU','FRI','SAT') NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)   REFERENCES rooms(room_id)     ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 7. ATTENDANCE
-- -------------------------------------------------------
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    date          DATE NOT NULL,
    status        ENUM('PRESENT','ABSENT','LATE') DEFAULT 'PRESENT',
    marked_by     INT,
    FOREIGN KEY (student_id) REFERENCES users(user_id)    ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by)  REFERENCES users(user_id)    ON DELETE SET NULL,
    UNIQUE KEY uq_attendance (student_id, course_id, date)
);

-- -------------------------------------------------------
-- 8. QUIZZES
-- -------------------------------------------------------
CREATE TABLE quizzes (
    quiz_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_id     INT NOT NULL,
    title         VARCHAR(100) NOT NULL,
    created_by    INT NOT NULL,
    total_marks   INT DEFAULT 10,
    scheduled_date DATE NULL,          -- optional due/exam date
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id)     ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 9. QUIZ QUESTIONS
-- -------------------------------------------------------
CREATE TABLE quiz_questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id     INT NOT NULL,
    question    VARCHAR(500) NOT NULL,
    option_a    VARCHAR(200),
    option_b    VARCHAR(200),
    option_c    VARCHAR(200),
    option_d    VARCHAR(200),
    correct_ans CHAR(1) NOT NULL,
    marks       INT DEFAULT 1,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 10. QUIZ SUBMISSIONS
-- -------------------------------------------------------
CREATE TABLE quiz_submissions (
    submission_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id       INT NOT NULL,
    student_id    INT NOT NULL,
    score         INT DEFAULT 0,
    submitted_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id)    REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id)   ON DELETE CASCADE,
    UNIQUE KEY uq_submission (quiz_id, student_id)
);

-- -------------------------------------------------------
-- 11. ISSUES
-- -------------------------------------------------------
CREATE TABLE issues (
    issue_id      INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    reported_by   INT NOT NULL,
    assigned_to   INT,
    room_id       INT,
    status        ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') DEFAULT 'OPEN',
    priority      ENUM('LOW','MEDIUM','HIGH') DEFAULT 'MEDIUM',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at   TIMESTAMP NULL,
    FOREIGN KEY (reported_by) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (room_id)     REFERENCES rooms(room_id) ON DELETE SET NULL
);

-- -------------------------------------------------------
-- 12. NOTIFICATIONS
-- -------------------------------------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT,
    is_read         TINYINT(1) DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 13. ANNOUNCEMENTS
-- -------------------------------------------------------
CREATE TABLE announcements (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    content         TEXT,
    posted_by       INT NOT NULL,
    target_role     ENUM('ALL','STUDENT','LECTURER','CR','TECHNICIAN') DEFAULT 'ALL',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 14. FEEDBACK (fully anonymous — no student_id stored)
-- -------------------------------------------------------
CREATE TABLE feedback (
    feedback_id  INT AUTO_INCREMENT PRIMARY KEY,
    course_id    INT NOT NULL,
    rating       INT CHECK (rating BETWEEN 1 AND 5),
    comments     TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

SELECT 'Schema created successfully.' AS status;
