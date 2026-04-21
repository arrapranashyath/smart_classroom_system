-- ============================================================
--  Smart Classroom Management System — Sample Data
--  Run AFTER schema.sql
-- ============================================================

USE smrtclass;

-- -------------------------------------------------------
-- USERS
-- Student usernames are their MIS numbers.
-- Student emails follow: lowercase-first-name + last-3-digits-of-MIS + @gmail.com
-- Student passwords follow: lowercase-first-name + @ + last-3-digits-of-MIS
-- Example: MIS 112415024, name Anushka Sujit Sinha
-- Email: anushka024@gmail.com, Password: anushka@024
-- Passwords are stored as SHA-256 using MySQL SHA2(...,256).
-- -------------------------------------------------------
INSERT INTO users (username, password, full_name, email, role) VALUES
-- ADMIN
('admin',      SHA2('pass123',256), 'Admin','admin@college.edu',  'ADMIN'),
-- LECTURERS
('prof_salve',    SHA2('pass123',256), 'Prof. Srikanth Salve',   'salve@college.edu',      'LECTURER'),
('prof_nair',     SHA2('pass123',256), 'Prof. Priya Nair',       'nair@college.edu',       'LECTURER'),
('prof_verma',    SHA2('pass123',256), 'Prof. Anil Verma',       'verma@college.edu',      'LECTURER'),
('prof_desai',    SHA2('pass123',256), 'Prof. Rohan Desai',      'desai@college.edu',      'LECTURER'),
('prof_iyer',     SHA2('pass123',256), 'Prof. Kavitha Iyer',     'iyer@college.edu',       'LECTURER'),
('prof_kulkarni', SHA2('pass123',256), 'Prof. Meera Kulkarni',   'kulkarni@college.edu',   'LECTURER'),
('prof_chaki',    SHA2('pass123',256), 'Prof. Sanga Chaki',      'chaki@college.edu',      'LECTURER'),
-- CLASS REPRESENTATIVES
('cr_ravi',     SHA2('pass123',256), 'Ravi Patil',          'ravi.cr@college.edu',    'CR'),
('cr_sneha',    SHA2('pass123',256), 'Sneha Joshi',         'sneha.cr@college.edu',   'CR'),
-- STUDENTS
('112415001', SHA2('a@001',256), 'A Sanjhanaa',                     'a001@gmail.com',                     'STUDENT'),
('112415002', SHA2('aadit@002',256), 'Aadit Bajaj',                 'aadit002@gmail.com',                 'STUDENT'),
('112415003', SHA2('aaonush@003',256), 'Aaonush Surana',            'aaonush003@gmail.com',               'STUDENT'),
('112415004', SHA2('aarohi@004',256), 'Aarohi Anup Junghare',       'aarohi004@gmail.com',                'STUDENT'),
('112415005', SHA2('abhay@005',256), 'Abhay Anand Gugale',          'abhay005@gmail.com',                 'STUDENT'),
('112415006', SHA2('abhinav@006',256), 'Abhinav Rathor',            'abhinav006@gmail.com',               'STUDENT'),
('112415007', SHA2('abhyankar@007',256), 'Abhyankar Pratap Singh Chauhan', 'abhyankar007@gmail.com',    'STUDENT'),
('112415008', SHA2('adarsh@008',256), 'Adarsh Dhananjayan Purupuruthan', 'adarsh008@gmail.com',      'STUDENT'),
('112415009', SHA2('adarsh@009',256), 'Adarsh Kumar',               'adarsh009@gmail.com',                'STUDENT'),
('112415010', SHA2('aditi@010',256), 'Aditi Thakre',                'aditi010@gmail.com',                 'STUDENT'),
('112415011', SHA2('aditya@011',256), 'Aditya',                     'aditya011@gmail.com',                'STUDENT'),
('112415012', SHA2('aditya@012',256), 'Aditya Jaiswal',             'aditya012@gmail.com',                'STUDENT'),
('112415013', SHA2('agrawal@013',256), 'Agrawal Meet Ashok',        'agrawal013@gmail.com',               'STUDENT'),
('112415014', SHA2('ajit@014',256), 'Ajit Kumar Dangi',             'ajit014@gmail.com',                  'STUDENT'),
('112415015', SHA2('akanksha@015',256), 'Akanksha Patidar',         'akanksha015@gmail.com',              'STUDENT'),
('112415016', SHA2('alok@016',256), 'Alok Jha',                     'alok016@gmail.com',                  'STUDENT'),
('112415017', SHA2('aman@017',256), 'Aman Kumar Maurya',            'aman017@gmail.com',                  'STUDENT'),
('112415018', SHA2('amol@018',256), 'Amol Raj',                     'amol018@gmail.com',                  'STUDENT'),
('112415019', SHA2('amritanshu@019',256), 'Amritanshu Mishra',      'amritanshu019@gmail.com',            'STUDENT'),
('112415020', SHA2('anay@020',256), 'Anay Goyal',                   'anay020@gmail.com',                  'STUDENT'),
('112415021', SHA2('aniket@021',256), 'Aniket Sinha',               'aniket021@gmail.com',                'STUDENT'),
('112415022', SHA2('anshika@022',256), 'Anshika Dhuria',            'anshika022@gmail.com',               'STUDENT'),
('112415023', SHA2('anuj@023',256), 'Anuj Jayendrakumar Shukla',    'anuj023@gmail.com',                  'STUDENT'),
('112415024', SHA2('anushka@024',256), 'Anushka Sujit Sinha',       'anushka024@gmail.com',               'STUDENT'),
('112415025', SHA2('apoorva@025',256), 'Apoorva Manoj',             'apoorva025@gmail.com',               'STUDENT'),
('112415026', SHA2('arjun@026',256), 'Arjun Mukhraiya',             'arjun026@gmail.com',                 'STUDENT'),
('112415027', SHA2('arman@027',256), 'Arman Ansari',                'arman027@gmail.com',                 'STUDENT'),
('112415028', SHA2('arman@028',256), 'Arman Gupta',                 'arman028@gmail.com',                 'STUDENT'),
('112415029', SHA2('arpit@029',256), 'Arpit Sharma',                'arpit029@gmail.com',                 'STUDENT'),
('112415030', SHA2('arra@030',256), 'Arra Pranashyath',             'arra030@gmail.com',                  'STUDENT'),
('112415031', SHA2('aryan@031',256), 'Aryan Bhandari',              'aryan031@gmail.com',                 'STUDENT'),
('112415032', SHA2('aryan@032',256), 'Aryan Anand Pardeshi',        'aryan032@gmail.com',                 'STUDENT'),
('112415033', SHA2('aryan@033',256), 'Aryan Kumar',                 'aryan033@gmail.com',                 'STUDENT'),
('112415034', SHA2('aryan@034',256), 'Aryan Nirvan',                'aryan034@gmail.com',                 'STUDENT'),
('112415035', SHA2('ashutosh@035',256), 'Ashutosh Bunkar',          'ashutosh035@gmail.com',              'STUDENT'),
('112415036', SHA2('ashutosh@036',256), 'Ashutosh Raj Singh',       'ashutosh036@gmail.com',              'STUDENT'),
('112415037', SHA2('ayush@037',256), 'Ayush Anand Kamble',          'ayush037@gmail.com',                 'STUDENT'),
('112415038', SHA2('ayush@038',256), 'Ayush Hotkar',                'ayush038@gmail.com',                 'STUDENT'),
('112415039', SHA2('ayush@039',256), 'Ayush Ramesh Sabale',         'ayush039@gmail.com',                 'STUDENT'),
('112415040', SHA2('banoth@040',256), 'Banoth Thrinadh Kumar',      'banoth040@gmail.com',                'STUDENT'),
('112415041', SHA2('beesabathina@041',256), 'Beesabathina Durga Mallikarjuna', 'beesabathina041@gmail.com', 'STUDENT'),
-- TECHNICIANS
('tech_suresh',  SHA2('pass123',256), 'Suresh Kumar',       'suresh@college.edu',     'TECHNICIAN'),
('tech_ramesh',  SHA2('pass123',256), 'Ramesh Pillai',      'ramesh@college.edu',     'TECHNICIAN');

-- -------------------------------------------------------
-- ROOMS
-- LH7  → Room 101 (lecture hall, theory classes)
-- CL5  → Lab A    (CD Lab G1, Java Lab G1)
-- CL6  → Lab B    (CN Lab G1)
-- CL8  → Lab C    (OS Lab G1)
-- -------------------------------------------------------
INSERT INTO rooms (room_name, capacity, room_type) VALUES
('LH7',   60, 'CLASSROOM'),
('Room 102',   60, 'CLASSROOM'),
('Room 201',   45, 'CLASSROOM'),
('Seminar Hall', 100, 'SEMINAR'),
('CL5',      30, 'LAB'),
('CL6',      30, 'LAB'),
('CL8',      30, 'LAB');

-- -------------------------------------------------------
-- ROOM → TECHNICIAN MAPPING
-- tech_suresh (user_id 52) handles rooms 1,2,3,4
-- tech_ramesh (user_id 53) handles rooms 5,6,7
-- -------------------------------------------------------
INSERT INTO room_technicians (room_id, technician_id) VALUES
(1, 52), (2, 52), (3, 52), (4, 52),
(5, 53), (6, 53), (7, 53);

-- -------------------------------------------------------
-- COURSES
-- lecturer_id mapping (user_id order: admin=1, prof_salve=2, prof_nair=3,
--   prof_verma=4, prof_desai=5, prof_iyer=6, prof_kulkarni=7, prof_chaki=8)
-- -------------------------------------------------------
INSERT INTO courses (course_code, course_name, lecturer_id) VALUES
('CS101', 'Discrete Structures',         3),   -- Prof. Nair      (Dr. Anagha Khiste)
('CS102', 'Operating Systems',           4),   -- Prof. Verma     (Dr. Sanjeev Sharma)
('CS103', 'Java Programming',            2),   -- Prof. Salve     (Dr. Shrikant Salve)
('CS105', 'Computer Networks',           6),   -- Prof. Iyer      (Dr. Habila Basumatary)
('CS106', 'Compiler Design',             7),   -- Prof. Kulkarni  (Dr. Kaptan Singh)
('CS107', 'Artificial Intelligence',     8);   -- Prof. Chaki     (Dr. Sanga Chaki)

-- -------------------------------------------------------
-- ENROLLMENTS
-- course_id: 1=DS, 2=OS, 3=Java, 4=CN, 5=CD, 6=AI
-- G1 students (ids 10-14) + CRs (ids 9=cr_ravi, 10=cr_sneha... 
-- Note: cr_ravi=9, cr_sneha=10 per INSERT order; students start at 11)
-- -------------------------------------------------------
INSERT INTO enrollments (student_id, course_id) VALUES
-- CR Ravi (id 9)
(9,1),(9,2),(9,3),(9,4),(9,5),(9,6),
-- CR Sneha (id 10)
(10,1),(10,2),(10,3),(10,4),(10,5),(10,6),
-- A Sanjhanaa (id 11)
(11,1),(11,2),(11,3),(11,4),(11,5),(11,6),
-- Aadit Bajaj (id 12)
(12,1),(12,2),(12,3),(12,4),(12,5),(12,6),
-- Aaonush Surana (id 13)
(13,1),(13,2),(13,3),(13,4),(13,5),(13,6),
-- Aarohi Anup Junghare (id 14)
(14,1),(14,2),(14,3),(14,4),(14,5),(14,6),
-- Abhay Anand Gugale (id 15)
(15,1),(15,2),(15,3),(15,4),(15,5),(15,6);

-- -------------------------------------------------------
-- TIMETABLE — 4th Sem CSE Section A, AY 2025-26, G1 only
-- Room IDs: 1=LH7 (theory), 5=CL5, 6=CL6, 7=CL8
-- course_id: 1=DS, 2=OS, 3=Java, 4=CN, 5=CD, 6=AI
--
-- Theory slots (same for all groups, held in LH7):
--   MON: CD 09:30-10:30, OS 10:30-11:30, AI 11:30-12:30
--   TUE: OS 09:30-10:30, CN 10:30-11:30, AI 11:30-12:30
--   WED: CN 09:30-10:30, OS 10:30-11:30, AI 11:30-12:30, DS 15:30-16:30
--   THU: DS 09:30-10:30, Java 10:30-11:30, CD 11:30-12:30
--   FRI: Java 09:30-10:30, CN 10:30-11:30, CD 11:30-12:30, DS 13:30-14:30
--
-- G1 Lab slots:
--   MON: CD Lab G1  13:30-15:30 (CL5), Java Lab G1 15:30-17:30 (CL5)
--   TUE: OS Lab G1  15:30-17:30 (CL8)
--   WED: CN Lab G1  13:30-15:30 (CL6)
-- -------------------------------------------------------
INSERT INTO timetable (course_id, room_id, day_of_week, start_time, end_time) VALUES
-- MONDAY
(5, 1, 'MON', '09:30:00', '10:30:00'),   -- CD theory
(2, 1, 'MON', '10:30:00', '11:30:00'),   -- OS theory
(6, 1, 'MON', '11:30:00', '12:30:00'),   -- AI theory
(5, 5, 'MON', '13:30:00', '15:30:00'),   -- CD Lab G1 (CL5)
(3, 5, 'MON', '15:30:00', '17:30:00'),   -- Java Lab G1 (CL5)
-- TUESDAY
(2, 1, 'TUE', '09:30:00', '10:30:00'),   -- OS theory
(4, 1, 'TUE', '10:30:00', '11:30:00'),   -- CN theory
(6, 1, 'TUE', '11:30:00', '12:30:00'),   -- AI theory
(2, 7, 'TUE', '15:30:00', '17:30:00'),   -- OS Lab G1 (CL8)
-- WEDNESDAY
(4, 1, 'WED', '09:30:00', '10:30:00'),   -- CN theory
(2, 1, 'WED', '10:30:00', '11:30:00'),   -- OS theory
(6, 1, 'WED', '11:30:00', '12:30:00'),   -- AI theory
(4, 6, 'WED', '13:30:00', '15:30:00'),   -- CN Lab G1 (CL6)
(1, 1, 'WED', '15:30:00', '16:30:00'),   -- DS theory
-- THURSDAY
(1, 1, 'THU', '09:30:00', '10:30:00'),   -- DS theory
(3, 1, 'THU', '10:30:00', '11:30:00'),   -- Java theory
(5, 1, 'THU', '11:30:00', '12:30:00'),   -- CD theory
-- FRIDAY
(3, 1, 'FRI', '09:30:00', '10:30:00'),   -- Java theory
(4, 1, 'FRI', '10:30:00', '11:30:00'),   -- CN theory
(5, 1, 'FRI', '11:30:00', '12:30:00'),   -- CD theory
(1, 1, 'FRI', '13:30:00', '14:30:00');   -- DS theory

-- -------------------------------------------------------
-- ATTENDANCE (sample records)
-- course_id: 1=DS, 2=OS, 3=Java, 4=CN, 5=CD, 6=AI
-- marked_by: 2=prof_salve, 3=prof_nair, 4=prof_verma,
--            6=prof_iyer, 7=prof_kulkarni, 8=prof_chaki
-- -------------------------------------------------------
INSERT INTO attendance (student_id, course_id, date, status, marked_by) VALUES
-- DS (Discrete Structures) — Prof. Nair (id 3) — Thu Jan 8
(11,1,'2026-01-08','PRESENT',3),
(12,1,'2026-01-08','PRESENT',3),
(13,1,'2026-01-08','ABSENT', 3),
(14,1,'2026-01-08','PRESENT',3),
-- OS (Operating Systems) — Prof. Verma (id 4) — Mon Jan 5
(11,2,'2026-01-05','PRESENT',4),
(12,2,'2026-01-05','PRESENT',4),
(15,2,'2026-01-05','ABSENT', 4),
-- Java Programming — Prof. Salve (id 2) — Thu Jan 8
(11,3,'2026-01-08','PRESENT',2),
(12,3,'2026-01-08','PRESENT',2),
(14,3,'2026-01-08','LATE',   2),
(15,3,'2026-01-08','PRESENT',2),
-- CN (Computer Networks) — Prof. Iyer (id 6) — Tue Jan 6
(12,4,'2026-01-06','PRESENT',6),
(13,4,'2026-01-06','LATE',   6),
(14,4,'2026-01-06','PRESENT',6),
(15,4,'2026-01-06','ABSENT', 6),
-- CD (Compiler Design) — Prof. Kulkarni (id 7) — Mon Jan 5
(11,5,'2026-01-05','PRESENT',7),
(13,5,'2026-01-05','PRESENT',7),
(14,5,'2026-01-05','ABSENT', 7),
-- AI (Artificial Intelligence) — Prof. Chaki (id 8) — Mon Jan 5
(11,6,'2026-01-05','PRESENT',8),
(12,6,'2026-01-05','LATE',   8),
(15,6,'2026-01-05','PRESENT',8);

-- -------------------------------------------------------
-- QUIZZES
-- course_id: 1=DS, 2=OS, 3=Java, 4=CN, 5=CD, 6=AI
-- created_by: 2=prof_salve, 3=prof_nair, 4=prof_verma,
--             6=prof_iyer, 7=prof_kulkarni, 8=prof_chaki
-- -------------------------------------------------------
INSERT INTO quizzes (course_id, title, created_by, total_marks) VALUES
(1, 'DS — Unit 1: Logic, Sets & Relations',      3, 10),
(2, 'Operating Systems Basics Quiz',             4, 15),
(3, 'Java OOP Concepts Quiz',                    2, 15),
(4, 'Computer Networks Fundamentals Quiz',       6, 15),
(5, 'Compiler Design: Lexical Analysis Quiz',    7, 10),
(6, 'Artificial Intelligence Basics Quiz',       8, 10);

-- -------------------------------------------------------
-- QUIZ QUESTIONS
-- -------------------------------------------------------
-- Quiz 1 (CS101)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(1, 'Which symbol is commonly used for logical AND?',
 'v', '^', '->', '~', 'B', 2),
(1, 'A set that contains no elements is called?',
 'Universal set', 'Finite set', 'Empty set', 'Power set', 'C', 2),
(1, 'If A={1,2} and B={2,3}, then A union B is?',
 '{1,2}', '{2}', '{1,2,3}', '{1,3}', 'C', 2),
(1, 'A relation on a set is a subset of?',
 'The power set only', 'The Cartesian product', 'The union of sets', 'The intersection of sets', 'B', 2),
(1, 'A statement that is always true is called?',
 'Contradiction', 'Tautology', 'Fallacy', 'Inference', 'B', 2);

-- Quiz 2 (Operating Systems)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(2, 'Which of the following is an operating system?',
 'MySQL', 'Linux', 'Python', 'HTML', 'B', 3),
(2, 'CPU scheduling is handled by which part of the OS?',
 'Process management', 'File management', 'Memory management', 'Device management', 'A', 3),
(2, 'Which memory is volatile?',
 'ROM', 'Hard disk', 'RAM', 'SSD', 'C', 3),
(2, 'Deadlock occurs when processes are?',
 'Running sequentially', 'Waiting indefinitely for resources', 'Using too much CPU', 'Terminated by the OS', 'B', 3),
(2, 'The smallest unit of CPU execution is called a?',
 'Program', 'Process', 'Thread', 'File', 'C', 3);

-- Quiz 3 (Java)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(3, 'Which OOP concept allows one class to acquire properties of another?',
 'Encapsulation', 'Inheritance', 'Abstraction', 'Polymorphism', 'B', 3),
(3, 'Which keyword is used to create an object in Java?',
 'class', 'void', 'new', 'this', 'C', 3),
(3, 'Which method is the entry point of a Java application?',
 'start()', 'run()', 'main()', 'init()', 'C', 3),
(3, 'Which access modifier makes a member visible only within the same class?',
 'public', 'private', 'protected', 'static', 'B', 3),
(3, 'Method overloading is an example of?',
 'Runtime polymorphism', 'Compile-time polymorphism', 'Inheritance', 'Abstraction', 'B', 3);

-- Quiz 4 (Computer Networks — CS105 → now course_id 4)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(4, 'What does LAN stand for?',
 'Large Area Network', 'Local Area Network', 'Long Area Node', 'Local Access Node', 'B', 3),
(4, 'Which device forwards packets between networks?',
 'Switch', 'Hub', 'Router', 'Repeater', 'C', 3),
(4, 'Which protocol is used to load web pages?',
 'FTP', 'HTTP', 'SMTP', 'ARP', 'B', 3),
(4, 'IP address version 4 uses how many bits?',
 '16', '32', '64', '128', 'B', 3),
(4, 'Which layer of the OSI model handles routing?',
 'Transport', 'Data Link', 'Network', 'Session', 'C', 3);

-- Quiz 5 (Compiler Design — CS106 → now course_id 5)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(5, 'Which phase of a compiler converts source code into tokens?',
 'Syntax Analysis', 'Lexical Analysis', 'Semantic Analysis', 'Code Generation', 'B', 2),
(5, 'A token is?',
 'A variable name only', 'The smallest meaningful unit in source code', 'A line of code', 'A function call', 'B', 2),
(5, 'Which data structure is used in syntax analysis?',
 'Queue', 'Stack', 'Parse Tree', 'Hash Table', 'C', 2),
(5, 'Regular expressions are used in which compiler phase?',
 'Code Generation', 'Semantic Analysis', 'Lexical Analysis', 'Optimization', 'C', 2),
(5, 'A context-free grammar is used to define?',
 'Tokens', 'Syntax of a language', 'Semantics', 'Machine code', 'B', 2);

-- Quiz 6 (Artificial Intelligence — CS107 → now course_id 6)
INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(6, 'Which search algorithm uses a heuristic function?',
 'BFS', 'DFS', 'A* Search', 'Dijkstra', 'C', 2),
(6, 'What does AI stand for?',
 'Automated Intelligence', 'Artificial Intelligence', 'Automated Information', 'Artificial Information', 'B', 2),
(6, 'Which of the following is a supervised learning algorithm?',
 'K-Means', 'Decision Tree', 'DBSCAN', 'Apriori', 'B', 2),
(6, 'A neural network is inspired by?',
 'Computer circuits', 'The human brain', 'Database systems', 'Graph theory', 'B', 2),
(6, 'Which technique is used to avoid overfitting in ML?',
 'Boosting', 'Regularization', 'Clustering', 'Encoding', 'B', 2);

-- -------------------------------------------------------
-- ANNOUNCEMENTS
-- -------------------------------------------------------
INSERT INTO announcements (title, content, posted_by, target_role) VALUES
('Welcome Back Students!',
 'Welcome to the new semester. Please check your timetable and enroll in your courses.',
 1, 'ALL'),
('Assignment 1 Due — CS101',
 'Discrete Structures Assignment 1 (Logic and Sets) is due this Friday. Submit on the portal.',
 3, 'STUDENT'),
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
(10, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(11, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(12, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(13, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(14, 'Welcome!', 'Welcome to Smart Classroom Management System.'),
(10, 'New Announcement', 'Assignment 1 Due — DS'),
(11, 'New Announcement', 'Assignment 1 Due — DS'),
(52, 'New Issue Assigned', 'Projector not working in LH7');

-- -------------------------------------------------------
-- ISSUES
-- room_id: 1=LH7, 5=CL5, 6=CL6, 7=CL8
-- assigned_to: 53=tech_suresh, 54=tech_ramesh
-- -------------------------------------------------------
INSERT INTO issues (title, description, reported_by, assigned_to, room_id, status, priority) VALUES
('Projector not working',
 'The projector in LH7 has no display output.',
 3, 52, 1, 'OPEN', 'HIGH'),
('AC not cooling',
 'Air conditioning in LH7 is on but not cooling.',
 4, 52, 1, 'IN_PROGRESS', 'MEDIUM'),
('Whiteboard marker missing',
 'No markers available in LH7.',
 9, 52, 1, 'RESOLVED', 'LOW'),
('Mic not working',
 'The wireless microphone in Seminar Hall produces static noise.',
 2, 53, 4, 'OPEN', 'HIGH'),
('Computer not booting',
 'A computer in CL5 shows a black screen on startup.',
 6, 53, 5, 'IN_PROGRESS', 'MEDIUM');

-- -------------------------------------------------------
-- FEEDBACK (anonymous — no student_id)
-- course_id: 1=DS, 2=OS, 3=Java, 4=CN, 5=CD, 6=AI
-- -------------------------------------------------------
INSERT INTO feedback (course_id, rating, comments) VALUES
(1, 5, 'Excellent explanation of logic and set theory. Very clear.'),
(1, 4, 'Good pace. Would like more practice problems on relations.'),
(1, 3, 'Slides need more worked examples on truth tables.'),
(2, 5, 'Operating Systems concepts explained very well.'),
(2, 4, 'Process scheduling topic was a bit rushed.'),
(3, 5, 'Java lab sessions are very hands-on and useful.'),
(3, 4, 'More examples on inheritance would help.'),
(4, 5, 'Computer Networks basics were easy to follow and practical.'),
(4, 4, 'Subnetting practice questions would help.'),
(5, 5, 'Compiler Design lectures are well structured and easy to follow.'),
(5, 4, 'More examples on parse trees would be helpful.'),
(6, 5, 'AI concepts explained very clearly with good examples.'),
(6, 4, 'Would like more practical ML examples in class.');

-- -------------------------------------------------------
-- QUIZ SUBMISSIONS (students who already took some quizzes)
-- -------------------------------------------------------
INSERT INTO quiz_submissions (quiz_id, student_id, score) VALUES
(1, 11,  8),
(1, 12,  6),
(1, 14, 10),
(2, 11, 12),
(2, 12,  9),
(3, 11, 13),
(3, 14, 15),
(4, 13, 12),
(4, 15,  9),
(5, 12,  8),
(5, 14,  6);

SELECT 'Sample data loaded successfully.' AS status;
SELECT CONCAT('Users: ', COUNT(*)) AS info FROM users;
SELECT CONCAT('Courses: ', COUNT(*)) AS info FROM courses;
SELECT CONCAT('Timetable slots: ', COUNT(*)) AS info FROM timetable;
SELECT CONCAT('Attendance records: ', COUNT(*)) AS info FROM attendance;
