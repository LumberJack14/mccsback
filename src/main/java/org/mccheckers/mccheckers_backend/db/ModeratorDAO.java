package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.PersonalData;

import java.sql.*;

public class ModeratorDAO {

    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static int create(int userId) {
        String sql = "INSERT INTO moderator (_user_id) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Moderator successfully created!");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            } else {
                System.out.println("Moderator creation failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while creating Moderator: " + e.getMessage());
        }
        return -1;
    }

    public static boolean remove(int userId) {
        String sql = "DELETE FROM moderator WHERE _user_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Moderator successfully deleted!");
                return true;
            } else {
                System.out.println("Moderator deletion failed. (No rows affected)");
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting moderator: " + e.getMessage());
        }

        return false;
    }

    public static boolean isModerator(int userId) {
        String sql = "SELECT COUNT(*) FROM moderator WHERE _user_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while checking moderator status: " + e.getMessage());
        }
        return false;
    }
}
