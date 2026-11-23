package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDao {

    public List<Book> findAll() throws SQLException {
        String sql = "SELECT id, isbn, title, author, category, total_copies, available_copies FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                books.add(book);
            }
        }

        return books;
    }

    public Optional<Book> findById(int bookId) throws SQLException {
        String sql = "SELECT id, isbn, title, author, category, total_copies, available_copies FROM books WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setCategory(rs.getString("category"));
                    book.setTotalCopies(rs.getInt("total_copies"));
                    book.setAvailableCopies(rs.getInt("available_copies"));
                    return Optional.of(book);
                }
            }
        }

        return Optional.empty();
    }

    public int create(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, category, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getCategory());
            statement.setInt(5, book.getTotalCopies());
            statement.setInt(6, book.getAvailableCopies());
            return statement.executeUpdate();
        }
    }

    public int update(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, category=?, total_copies=?, available_copies=? WHERE id=?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getCategory());
            statement.setInt(5, book.getTotalCopies());
            statement.setInt(6, book.getAvailableCopies());
            statement.setInt(7, book.getId());
            return statement.executeUpdate();
        }
    }

    public int delete(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            return statement.executeUpdate();
        }
    }

    public List<Book> search(String query) throws SQLException {
        String sql = "SELECT id, isbn, title, author, category, total_copies, available_copies FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setCategory(rs.getString("category"));
                    book.setTotalCopies(rs.getInt("total_copies"));
                    book.setAvailableCopies(rs.getInt("available_copies"));
                    books.add(book);
                }
            }
        }

        return books;
    }
}

