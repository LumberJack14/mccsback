package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.User;

import java.sql.*;

public class UserDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static int createUser(
            String username,
            int elo,
            String passwordHash,
            String name,
            String surname,
            String phoneNumber,
            int countryId
    ) {
        int personalDataId = PersonalDataDAO.createPersonalData(name, surname, phoneNumber, countryId);
        if (personalDataId == -1) {
            System.err.println("No personal data");
            return -1;
        }
        String insertUserSQL = "INSERT INTO _user (username, elo, pass_hash, personal_data_id, active) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, elo);
            preparedStatement.setString(3, passwordHash);
            preparedStatement.setInt(4, personalDataId);
            preparedStatement.setBoolean(5, false);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User successfully created!");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            } else {
                System.out.println("User creation failed. (no rows affected)");
            }

        } catch (SQLException e) {
            System.err.println("Error while creating user: " + e.getMessage());
        }
        return -1;
    }

    public static User findByUsername(String username) {
        String selectSQL = "SELECT id, username, elo, pass_hash, personal_data_id, active FROM _user WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setElo(resultSet.getInt("elo"));
                    user.setPasswordHash(resultSet.getString("pass_hash"));
                    user.setActive(resultSet.getBoolean("active"));

                    return user;
                } else {
                    System.out.println("User with username " + username + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving user: " + e.getMessage());
        }

        return null;
    }

    public static User findById(int id) {
        String selectSQL = "SELECT id, username, elo, pass_hash, personal_data_id, active FROM _user WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setElo(resultSet.getInt("elo"));
                    user.setPasswordHash(resultSet.getString("pass_hash"));
                    user.setActive(resultSet.getBoolean("active"));

                    return user;
                } else {
                    System.out.println("User with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving user: " + e.getMessage());
        }

        return null;
    }

    public static boolean update(User user) {
        String updateSQL = "UPDATE _user SET username = ?, elo = ?, pass_hash = ?, active = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            // Set the updated fields
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, user.getElo());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setBoolean(4, user.isActive());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User successfully updated!");
                return true;
            } else {
                System.out.println("No user found with id " + user.getId() + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("Error while updating user: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteUserById(int userId) {
        String deleteUserSQL = "DELETE FROM _user WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSQL)) {

            preparedStatement.setInt(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User successfully deleted!");
                return true;
            } else {
                System.out.println("User deletion failed. (No rows affected)");
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting user: " + e.getMessage());
        }

        return false;
    }

}