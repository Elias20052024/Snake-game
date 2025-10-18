package com.snake.database;

import com.snake.model.Score;
import com.snake.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:snake.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create database connection
            connection = DriverManager.getConnection(DB_URL);
            
            // Create tables if they don't exist
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            )""";

        String createScoresTable = """
            CREATE TABLE IF NOT EXISTS scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                score INTEGER,
                level INTEGER,
                date_played TEXT,
                FOREIGN KEY(user_id) REFERENCES users(id)
            )""";
            
        String createGameStateTable = """
            CREATE TABLE IF NOT EXISTS game_state (
                user_id INTEGER PRIMARY KEY,
                current_level INTEGER NOT NULL DEFAULT 1,
                current_score INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(user_id) REFERENCES users(id)
            )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createScoresTable);
            stmt.execute(createGameStateTable);
        }
    }

    // User Management Methods
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Score Management Methods
    public void saveScore(int userId, int score, int level) {
        String sql = "INSERT INTO scores (user_id, score, level, date_played) VALUES (?, ?, ?, datetime('now'))";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, score);
            pstmt.setInt(3, level);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username 
            FROM scores s 
            JOIN users u ON s.user_id = u.id 
            ORDER BY s.score DESC 
            LIMIT ?""";
            
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                scores.add(new Score(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getInt("score"),
                    rs.getInt("level"),
                    rs.getString("date_played")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void saveGameState(int userId, int level, int score) {
        String sql = """
            INSERT INTO game_state (user_id, current_level, current_score)
            VALUES (?, ?, ?)
            ON CONFLICT(user_id)
            DO UPDATE SET current_level = ?, current_score = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, level);
            pstmt.setInt(3, score);
            pstmt.setInt(4, level);
            pstmt.setInt(5, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int[] loadGameState(int userId) {
        String sql = "SELECT current_level, current_score FROM game_state WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new int[] {
                    rs.getInt("current_level"),
                    rs.getInt("current_score")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[] {1, 0}; // Default values if no saved state
    }

    public void updateUserLevel(int userId, int level) {
        String sql = """
            UPDATE game_state 
            SET current_level = ?, current_score = 0 
            WHERE user_id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, level);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were updated, insert a new record
                saveGameState(userId, level, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}