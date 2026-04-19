-- ============================================================
--  Smart Classroom Management System — Sample Data
--  Run AFTER schema.sql
-- ============================================================

USE smrtclass;

-- -------------------------------------------------------
-- USERS
-- Passwords are SHA-256 of 'pass123'
-- SHA2('pass123', 256) = '9b8769a4a742959a2d0298c36fb70623f2a2d0f99c8c88640f85cf7c9'... 
-- We use Java's SHA-256, which matches MySQL's SHA2(...,256).
-- All users below have password: pass123
-- -------------------------------------------------------
INSERT INTO users (username, password, full_name, email, role) VALUES
-- ADMIN
('admin',      SHA2('pass123',256), 'Dr. Admin Singh',      'admin@college.edu',      'ADMIN'),
-- LECTURERS
('prof_sharma', SHA2('pass123',256), 'Prof. Ravi Sharma',   'sharma@college.edu',     'LECTURER'),
('prof_nair',   SHA2('pass123',256), 'Prof. Priya Nair',    'nair@college.edu',       'LECTURER'),
('prof_verma',  SHA2('pass123',256), 'Prof. Anil Verma',    'verma@college.edu',      'LECTURER'),
-- CLASS REPRESENTATIVES
('cr_ravi',     SHA2('pass123',256), 'Ravi Patil',          'ravi.cr@college.edu',    'CR'),
('cr_sneha',    SHA2('pass123',256), 'Sneha Joshi',         'sneha.cr@college.edu',   'CR'),
-- STUDENTS
('student1',    SHA2('pass123',256), 'Anjali Mehta',        'anjali@college.edu',     'STUDENT'),
('student2',    SHA2('pass123',256), 'Rohan Desai',         'rohan@college.edu',      'STUDENT'),
('student3',    SHA2('pass123',256), 'Pooja Kulkarni',      'pooja@college.edu',      'STUDENT'),
('student4',    SHA2('pass123',256), 'Arjun Reddy',         'arjun@college.edu',      'STUDENT'),
('student5',    SHA2('pass123',256), 'Meera Iyer',          'meera@college.edu',      'STUDENT'),
-- TECHNICIANS
('tech_suresh',  SHA2('pass123',256), 'Suresh Kumar',       'suresh@college.edu',     'TECHNICIAN'),
('tech_ramesh',  SHA2('pass123',256), 'Ramesh Pillai',      'ramesh@college.edu',     'TECHNICIAN');

-- -------------------------------------------------------
-- ROOMS
-- -------------------------------------------------------
INSERT INTO rooms (room_name, capacity, room_type) VALUES
('Room 101',   60, 'CLASSROOM'),
('Room 102',   60, 'CLASSROOM'),
('Room 201',   45, 'CLASSROOM'),
('Seminar Hall', 100, 'SEMINAR'),
('Lab A',      30, 'LAB'),
('Lab B',      30, 'LAB');

-- -------------------------------------------------------
-- ROOM → TECHNICIAN MAPPING
-- tech_suresh (user_id 12) handles rooms 1,2,3
-- tech_ramesh (user_id 13) handles rooms 4,5,6
-- -------------------------------------------------------
INSERT INTO room_technicians (room_id, technician_id) VALUES
(1, 12), (2, 12), (3, 12),
(4, 13), (5, 13), (6, 13);

-- -------------------------------------------------------
-- COURSES
-- -------------------------------------------------------
INSERT INTO courses (course_code, course_name, lecturer_id) VALUES
('CS101', 'Data Structures & Algorithms',   2),
('CS102', 'Database Management Systems',    3),
('CS103', 'Java Programming',               2),
('CS104', 'Operating Systems',              4),
('CS105', 'Computer Networks',              3);

-- -------------------------------------------------------
-- ENROLLMENTS  (students 7–11 enrolled in various courses)
-- -------------------------------------------------------
INSERT INTO enrollments (student_id, course_id) VALUES
-- Anjali Mehta
(7,1),(7,2),(7,3),(7,4),
-- Rohan Desai
(8,1),(8,2),(8,3),(8,5),
-- Pooja Kulkarni
(9,1),(9,2),(9,4),(9,5),
-- Arjun Reddy
(10,1),(10,3),(10,4),(10,5),
-- Meera Iyer
(11,2),(11,3),(11,4),(11,5),
-- CR Ravi enrolled too
(5,1),(5,2),(5,3),
-- CR Sneha enrolled too
(6,2),(6,4),(6,5);

-- -------------------------------------------------------
-- TIMETABLE
-- -------------------------------------------------------
INSERT INTO timetable (course_id, room_id, day_of_week, start_time, end_time) VALUES
(1, 1, 'MON', '09:00:00', '10:00:00'),
(1, 1, 'WED', '09:00:00', '10:00:00'),
(2, 2, 'MON', '11:00:00', '12:00:00'),
(2, 2, 'THU', '11:00:00', '12:00:00'),
(3, 5, 'TUE', '14:00:00', '16:00:00'),
(3, 5, 'FRI', '14:00:00', '16:00:00'),
(4, 3, 'WED', '11:00:00', '12:00:00'),
(4, 3, 'FRI', '09:00:00', '10:00:00'),
(5, 2, 'TUE', '09:00:00', '10:00:00'),
(5, 2, 'SAT', '10:00:00', '11:00:00');

-- -------------------------------------------------------
-- ATTENDANCE (sample records)
-- -------------------------------------------------------
INSERT INTO attendance (student_id, course_id, date, status, marked_by) VALUES
-- CS101 — Mon Jan 6
(7,1,'2025-01-06','PRESENT',2),
(8,1,'2025-01-06','PRESENT',2),
(9,1,'2025-01-06','ABSENT', 2),
(10,1,'2025-01-06','PRESENT',2),
(5,1,'2025-01-06','PRESENT',2),
-- CS101 — Wed Jan 8
(7,1,'2025-01-08','PRESENT',2),
(8,1,'2025-01-08','LATE',   2),
(9,1,'2025-01-08','PRESENT',2),
(10,1,'2025-01-08','ABSENT',2),
(5,1,'2025-01-08','PRESENT',2),
-- CS102 — Mon Jan 6
(7,2,'2025-01-06','PRESENT',3),
(8,2,'2025-01-06','PRESENT',3),
(11,2,'2025-01-06','ABSENT',3),
-- CS103 — Tue Jan 7
(7,3,'2025-01-07','PRESENT',2),
(8,3,'2025-01-07','PRESENT',2),
(10,3,'2025-01-07','LATE',  2),
(11,3,'2025-01-07','PRESENT',2);

-- -------------------------------------------------------
-- QUIZZES
-- -------------------------------------------------------
INSERT INTO quizzes (course_id, title, created_by, total_marks) VALUES
(1, 'CS101 — Unit 1: Arrays & Linked Lists', 2, 10),
(2, 'DBMS Mid-Term: ER & Normalization',     3, 20),
(3, 'Java OOP Concepts Quiz',                2, 15);

-- -------------------------------------------------------
-- QUIZ QUESTIONS
-- -------------------------------------------------------
-- Quiz 1 (CS101)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(1, 'Which data structure uses LIFO?',
 'Queue', 'Stack', 'Tree', 'Graph', 'B', 2),
(1, 'Time complexity of binary search?',
 'O(n)', 'O(log n)', 'O(n²)', 'O(1)', 'B', 2),
(1, 'A linked list node contains?',
 'Only data', 'Only pointer', 'Data and pointer', 'Neither', 'C', 2),
(1, 'Which is not a linear data structure?',
 'Array', 'Stack', 'Queue', 'Tree', 'D', 2),
(1, 'Best case for bubble sort?',
 'O(n²)', 'O(n log n)', 'O(n)', 'O(1)', 'C', 2);

-- Quiz 2 (DBMS)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(2, 'What does SQL stand for?',
 'Structured Query Language', 'Simple Query Language',
 'Sequential Query Language', 'Standard Query Language', 'A', 4),
(2, 'Which normal form removes partial dependencies?',
 '1NF', '2NF', '3NF', 'BCNF', 'B', 4),
(2, 'A primary key can be?',
 'NULL', 'Duplicate', 'Both NULL and Duplicate', 'Neither NULL nor Duplicate', 'D', 4),
(2, 'Which JOIN returns all records from both tables?',
 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL OUTER JOIN', 'D', 4),
(2, 'DDL stands for?',
 'Data Definition Language', 'Data Display Language',
 'Data Deletion Language', 'Dynamic Definition Language', 'A', 4);

-- -------------------------------------------------------
-- ANNOUNCEMENTS
-- -------------------------------------------------------
INSERT INTO announcements (title, content, posted_by, target_role) VALUES
('Welcome Back Students!',
 'Welcome to the new semester. Please check your timetable and enroll in your courses.',
 1, 'ALL'),
('Assignment 1 Due — CS101',
 'Data Structures Assignment 1 (Arrays) is due this Friday. Submit on the portal.',
 2, 'STUDENT'),
('Lab Session Change',
 'Java Programming lab on Tuesday has been shifted to Lab B due to maintenance.',
 2, 'STUDENT'),
('Faculty Meeting',
 'All lecturers are required to attend the faculty meeting on Friday at 3pm in the Seminar Hall.',
 1, 'LECTURER');

-- -------------------------------------------------------
-- NOTIFICATIONS
-- -------------------------------------------------------
INSERT INTO notifications (user_id, title, message) VALUES
(7,  'Welcome!', 'Welcome to Smart Classroom Management System.'),
(8,  'Welcome!', 'Welcome to Smart Classroom Management System.'),
(9,  'Welcome!', 'Welcome to Smart Classroom Management System.'),
(10, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(11, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(7,  'New Announcement', 'Assignment 1 Due — CS101'),
(8,  'New Announcement', 'Assignment 1 Due — CS101'),
(12, 'New Issue Assigned', 'Projector not working in Room 101');

-- -------------------------------------------------------
-- ISSUES
-- -------------------------------------------------------
INSERT INTO issues (title, description, reported_by, assigned_to, room_id, status, priority) VALUES
('Projector not working',
 'The projector in Room 101 has no display output.',
 2, 12, 1, 'OPEN', 'HIGH'),
('AC not cooling',
 'Air conditioning in Room 102 is on but not cooling.',
 3, 12, 2, 'IN_PROGRESS', 'MEDIUM'),
('Whiteboard marker missing',
 'No markers available in Room 201.',
 5, 12, 3, 'RESOLVED', 'LOW'),
('Mic not working',
 'The wireless microphone in Seminar Hall produces static noise.',
 2, 13, 4, 'OPEN', 'HIGH'),
('Computer #5 not booting',
 'Lab A Computer #5 shows a black screen on startup.',
 3, 13, 5, 'IN_PROGRESS', 'MEDIUM');

-- -------------------------------------------------------
-- FEEDBACK (anonymous — no student_id)
-- -------------------------------------------------------
INSERT INTO feedback (course_id, rating, comments) VALUES
(1, 5, 'Excellent explanation of sorting algorithms. Very clear.'),
(1, 4, 'Good pace. Would like more practice problems.'),
(1, 3, 'Slides need more diagrams.'),
(2, 5, 'Prof. Nair explains SQL queries very well.'),
(2, 4, 'Normalization topic was a bit rushed.'),
(3, 5, 'Java lab sessions are very hands-on and useful.'),
(3, 4, 'More examples on inheritance would help.');

-- -------------------------------------------------------
-- QUIZ SUBMISSIONS (students who already took quiz 1)
-- -------------------------------------------------------
INSERT INTO quiz_submissions (quiz_id, student_id, score) VALUES
(1, 7,  8),
(1, 8,  6),
(1, 10, 10);

SELECT 'Sample data loaded successfully.' AS status;
SELECT CONCAT('Users: ', COUNT(*)) AS info FROM users;
SELECT CONCAT('Courses: ', COUNT(*)) AS info FROM courses;
SELECT CONCAT('Timetable slots: ', COUNT(*)) AS info FROM timetable;
SELECT CONCAT('Attendance records: ', COUNT(*)) AS info FROM attendance;
