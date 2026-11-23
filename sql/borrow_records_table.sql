-- Create borrow_records table for tracking book borrowing and returning
CREATE TABLE IF NOT EXISTS borrow_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    book_title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('Active', 'Returned', 'Overdue', 'Reserved', 'Lost') DEFAULT 'Active',
    fine DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    INDEX idx_student_id (student_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date)
);

-- Add column to track available copies for lending (if not exists)
ALTER TABLE books ADD COLUMN IF NOT EXISTS available_copies INT DEFAULT 0;
