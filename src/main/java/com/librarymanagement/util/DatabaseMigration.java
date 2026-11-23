package com.librarymanagement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import com.librarymanagement.config.DatabaseConfig;

/**
 * Database migration utility to automatically create required tables on startup.
 * This ensures the database schema is always up-to-date.
 */
public class DatabaseMigration {
    private static final String DB_PROPERTIES_PATH = "/config/db.properties";
    private static final String SCHEMA_SQL_PATH = "/sql/schema.sql";
    private static final String SEED_SQL_PATH = "/sql/seed.sql";
    
    private static final String BORROW_RECORDS_TABLE_SQL = 
        "CREATE TABLE IF NOT EXISTS borrow_records (" +
        "    record_id INT AUTO_INCREMENT PRIMARY KEY," +
        "    student_id INT NOT NULL," +
        "    book_id INT NOT NULL," +
        "    book_title VARCHAR(255) NOT NULL," +
        "    author VARCHAR(100) NOT NULL," +
        "    borrow_date DATE NOT NULL," +
        "    due_date DATE NOT NULL," +
        "    return_date DATE," +
        "    status ENUM('Active', 'Returned', 'Overdue', 'Reserved', 'Lost') DEFAULT 'Active'," +
        "    fine DECIMAL(10, 2) DEFAULT 0.00," +
        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "    FOREIGN KEY (student_id) REFERENCES users(id)," +
        "    FOREIGN KEY (book_id) REFERENCES books(id)," +
        "    INDEX idx_student_id (student_id)," +
        "    INDEX idx_book_id (book_id)," +
        "    INDEX idx_status (status)," +
        "    INDEX idx_borrow_date (borrow_date)" +
        ")";
    
    private static final String ADD_AVAILABLE_COPIES_COLUMN = 
        "ALTER TABLE books ADD COLUMN IF NOT EXISTS available_copies INT DEFAULT 0";
    
    /**
     * Run all database migrations.
     * Creates tables if they don't exist.
     */
    public static void runMigrations() {
        ensureDatabaseExists();

        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("🔄 Starting database migrations...");

            if (!coreTablesExist(conn)) {
                System.out.println("📝 Creating base schema...");
                executeSqlScript(conn, SCHEMA_SQL_PATH);
                System.out.println("✅ Base schema created successfully!");
            } else {
                System.out.println("✅ Base schema already exists!");
            }
            
            // Check if borrow_records table exists
            if (!tableExists(conn, "borrow_records")) {
                System.out.println("📝 Creating borrow_records table...");
                executeSql(conn, BORROW_RECORDS_TABLE_SQL);
                System.out.println("✅ borrow_records table created successfully!");
            } else {
                System.out.println("✅ borrow_records table already exists!");
            }
            
            // Add available_copies column if missing
            System.out.println("📝 Adding available_copies column to books table...");
            executeSql(conn, ADD_AVAILABLE_COPIES_COLUMN);
            System.out.println("✅ books table updated successfully!");
            
            // Initialize book availability if needed
            System.out.println("📝 Initializing book availability...");
            String updateAvailability = 
                "UPDATE books SET available_copies = 5 WHERE available_copies = 0 OR available_copies IS NULL";
            try (Statement stmt = conn.createStatement()) {
                int rowsUpdated = stmt.executeUpdate(updateAvailability);
                if (rowsUpdated > 0) {
                    System.out.println("✅ Updated " + rowsUpdated + " books with availability!");
                }
            }

            if (shouldSeed(conn)) {
                System.out.println("📝 Seeding initial data...");
                executeSqlScript(conn, SEED_SQL_PATH);
                System.out.println("✅ Seed data inserted successfully!");
            } else {
                System.out.println("✅ Seed data already present, skipping.");
            }
            
            System.out.println("✅ All database migrations completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureDatabaseExists() {
        Properties props = loadDbProperties();
        String jdbcUrl = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalStateException("Missing db.url in " + DB_PROPERTIES_PATH);
        }

        String dbName = extractDatabaseName(jdbcUrl);
        String adminJdbcUrl = buildAdminJdbcUrl(jdbcUrl);

        String createDbSql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "`";
        try (Connection conn = DriverManager.getConnection(adminJdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createDbSql);
            System.out.println("✅ Database '" + dbName + "' is ready.");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create/check database '" + dbName + "': " + e.getMessage(), e);
        }
    }

    private static Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseMigration.class.getResourceAsStream(DB_PROPERTIES_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Missing DB config file: " + DB_PROPERTIES_PATH);
            }
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load DB properties from " + DB_PROPERTIES_PATH, e);
        }
    }

    private static String extractDatabaseName(String jdbcUrl) {
        int queryIndex = jdbcUrl.indexOf('?');
        String base = queryIndex >= 0 ? jdbcUrl.substring(0, queryIndex) : jdbcUrl;
        int lastSlash = base.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == base.length() - 1) {
            throw new IllegalStateException("Cannot determine database name from db.url: " + jdbcUrl);
        }
        return base.substring(lastSlash + 1);
    }

    private static String buildAdminJdbcUrl(String jdbcUrl) {
        int queryIndex = jdbcUrl.indexOf('?');
        String query = queryIndex >= 0 ? jdbcUrl.substring(queryIndex) : "";
        String base = queryIndex >= 0 ? jdbcUrl.substring(0, queryIndex) : jdbcUrl;

        int lastSlash = base.lastIndexOf('/');
        if (lastSlash < 0) {
            throw new IllegalStateException("Cannot build admin JDBC URL from db.url: " + jdbcUrl);
        }

        return base.substring(0, lastSlash + 1) + query;
    }

    private static boolean coreTablesExist(Connection conn) throws SQLException {
        return tableExists(conn, "users")
            && tableExists(conn, "books")
            && tableExists(conn, "members")
            && tableExists(conn, "loans");
    }

    private static boolean shouldSeed(Connection conn) throws SQLException {
        return !tableHasRows(conn, "users") || !tableHasRows(conn, "books") || !tableHasRows(conn, "members");
    }

    private static boolean tableHasRows(Connection conn, String tableName) throws SQLException {
        String query = "SELECT 1 FROM " + tableName + " LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        }
    }
    
    /**
     * Check if a table exists in the database.
     */
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        String query = "SELECT 1 FROM information_schema.tables WHERE table_schema = 'library_management' AND table_name = ?";
        try (var pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Execute a SQL statement.
     */
    private static void executeSql(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void executeSqlScript(Connection conn, String resourcePath) throws SQLException {
        String script = readClasspathFile(resourcePath);
        for (String statement : splitSqlStatements(script)) {
            executeSql(conn, statement);
        }
    }

    private static String readClasspathFile(String resourcePath) {
        try (InputStream input = DatabaseMigration.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing SQL script in resources: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.startsWith("#")) {
                        continue;
                    }
                    content.append(line).append('\n');
                }
                return content.toString();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read SQL script: " + resourcePath, e);
        }
    }

    private static List<String> splitSqlStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < script.length(); i++) {
            char ch = script.charAt(i);

            if (ch == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (ch == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }

            if (ch == ';' && !inSingleQuote && !inDoubleQuote) {
                String statement = current.toString().trim();
                if (!statement.isEmpty()) {
                    statements.add(statement);
                }
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        String tail = current.toString().trim();
        if (!tail.isEmpty()) {
            statements.add(tail);
        }

        return statements;
    }
    
    /**
     * Main method to run migrations from command line.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Database Migration Utility v1.0      ║");
        System.out.println("║   Library Management System            ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        
        runMigrations();
    }
}
