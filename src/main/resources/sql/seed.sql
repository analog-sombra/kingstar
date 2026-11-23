USE library_management;

INSERT IGNORE INTO users (id, name, email, password, registrationid, type, status)
VALUES
    (1, 'Aarav Student', 'student@library.local', 'Student@123', 'STU001', 'STUDENT', 'ACTIVE'),
    (2, 'Riya Teacher', 'teacher@library.local', 'Teacher@123', 'TCH001', 'TEACHER', 'ACTIVE');

INSERT IGNORE INTO books (id, isbn, title, author, category, total_copies, available_copies)
VALUES
    (1, '9780134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 5, 5),
    (2, '9780596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 4, 4),
    (3, '9781617294945', 'Spring in Action', 'Craig Walls', 'Programming', 5, 5),
    (4, '9780132350884', 'Clean Code', 'Robert C. Martin', 'Programming', 6, 6),
    (5, '9780134494166', 'Clean Architecture', 'Robert C. Martin', 'Programming', 5, 5),
    (6, '9781491950357', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Databases', 3, 3),
    (7, '9780201633610', 'Design Patterns', 'Erich Gamma', 'Software Engineering', 4, 4),
    (8, '9780131103627', 'The C Programming Language', 'Brian W. Kernighan', 'Programming', 3, 3),
    (9, '9780131177055', 'Modern Operating Systems', 'Andrew S. Tanenbaum', 'Computer Science', 4, 4),
    (10, '9780262033848', 'Introduction to Algorithms', 'Thomas H. Cormen', 'Algorithms', 5, 5),
    (11, '9780134093413', 'Computer Networks', 'Andrew S. Tanenbaum', 'Networking', 4, 4),
    (12, '9780134610993', 'Operating System Concepts', 'Abraham Silberschatz', 'Computer Science', 4, 4),
    (13, '9780135957059', 'Artificial Intelligence: A Modern Approach', 'Stuart Russell', 'AI', 3, 3),
    (14, '9780137903955', 'Database System Concepts', 'Abraham Silberschatz', 'Databases', 4, 4),
    (15, '9780137129294', 'Compilers: Principles, Techniques, and Tools', 'Alfred V. Aho', 'Computer Science', 2, 2),
    (16, '9781492078005', 'Kubernetes: Up and Running', 'Kelsey Hightower', 'DevOps', 5, 5),
    (17, '9781098104030', 'System Design Interview', 'Alex Xu', 'Software Engineering', 6, 6),
    (18, '9780134757599', 'Refactoring', 'Martin Fowler', 'Programming', 4, 4),
    (19, '9780321125217', 'Domain-Driven Design', 'Eric Evans', 'Software Engineering', 3, 3),
    (20, '9781492056355', 'Building Microservices', 'Sam Newman', 'Architecture', 4, 4),
    (21, '9781492072584', 'Learning SQL', 'Alan Beaulieu', 'Databases', 5, 5),
    (22, '9781491962299', 'Fluent Python', 'Luciano Ramalho', 'Programming', 4, 4);

INSERT IGNORE INTO members (id, full_name, phone, email, address)
VALUES
    (1, 'Aarav Sharma', '+91-9999999999', 'aarav@example.com', 'Mumbai');
