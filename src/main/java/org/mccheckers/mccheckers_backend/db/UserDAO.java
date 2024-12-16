package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTOLeaderboard;
import org.mccheckers.mccheckers_backend.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static boolean updateElo(int userId, int elo) {
        String updateSQL = "UPDATE _user SET elo = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            preparedStatement.setInt(1, elo);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Elo of the user successfully updated!");
                return true;
            } else {
                System.out.println("No user found with id " + userId + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("Error while updating elo for user: " + e.getMessage());
        }
        return false;
    }

    public static String getUserRank(int userId) {
        String sql = "SELECT get_user_rank(?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String rank = resultSet.getString("name");

                    return rank;
                } else {
                    System.out.println("User with id " + userId + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving user rank: " + e.getMessage());
        }

        return null;

    }

    public static List<UserResponseDTOLeaderboard> getLeaderboard(int limit) {
        String sql = "Select get_top_users_with_details(?)";
        List<UserResponseDTOLeaderboard> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, limit);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new UserResponseDTOLeaderboard(
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("username"),
                                    resultSet.getInt("elo"),
                                    resultSet.getString("rank_name"),
                                    resultSet.getInt("total_matches")
                            )
                    );

                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving leaderboards: " + e.getMessage());
        }

        return users;
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

    public static List<User> findUsersByUsernameSubstring(String substring) {
        List<User> users = new ArrayList<>();
        String searchSQL = "SELECT id, username, elo, pass_hash, personal_data_id, active FROM _user WHERE username LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(searchSQL)) {

            preparedStatement.setString(1, "%" + substring + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setElo(resultSet.getInt("elo"));
                    user.setPasswordHash(resultSet.getString("pass_hash"));
                    user.setActive(resultSet.getBoolean("active"));

                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while searching for users by username substring: " + e.getMessage());
        }

        return users;
    }


}
