package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public Optional<User> findActiveUserForLogin(String registrationId, String password, String type)
            throws SQLException {
        String sql = """
                SELECT id, name, email, password, registrationid, type, status
                FROM users
                WHERE registrationid = ?
                  AND password = ?
                  AND type = ?
                  AND status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println(statement);
            statement.setString(1, registrationId);
            statement.setString(2, password);
            statement.setString(3, type);
            System.out.println(statement);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRegistrationId(rs.getString("registrationid"));
                user.setType(rs.getString("type"));
                user.setStatus(rs.getString("status"));

                return Optional.of(user);
            }
        }
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT id, name, email, password, registrationid, type, status FROM users WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRegistrationId(rs.getString("registrationid"));
                    user.setType(rs.getString("type"));
                    user.setStatus(rs.getString("status"));
                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    public List<User> findAllStudents() throws SQLException {
        String sql = "SELECT id, name, email, password, registrationid, type, status FROM users WHERE type = 'STUDENT'";
        List<User> students = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRegistrationId(rs.getString("registrationid"));
                user.setType(rs.getString("type"));
                user.setStatus(rs.getString("status"));
                students.add(user);
            }
        }

        return students;
    }

    public int create(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, registrationid, type, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRegistrationId());
            statement.setString(5, user.getType());
            statement.setString(6, user.getStatus());
            return statement.executeUpdate();
        }
    }

    public int update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, email=?, password=?, registrationid=?, type=?, status=? WHERE id=?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRegistrationId());
            statement.setString(5, user.getType());
            statement.setString(6, user.getStatus());
            statement.setInt(7, user.getId());
            return statement.executeUpdate();
        }
    }

    public int delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate();
        }
    }
}

