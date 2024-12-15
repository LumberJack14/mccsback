package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.PersonalData;
import org.mccheckers.mccheckers_backend.model.User;

import java.sql.*;

public class PersonalDataDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static int createPersonalData(String name, String surname, String phoneNumber, int countryId) {
        String insertUserSQL = "INSERT INTO personal_data (name, surname, phone_number, country_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setInt(4, countryId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PersonalData successfully created!");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            } else {
                System.out.println("PersonalData creation failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while creating PersonalData: " + e.getMessage());
        }
        return -1;
    }

    public static PersonalData getPersonalDataById(int id) {
        String selectSQL = "SELECT name, surname, phone_number, avatar_link FROM personal_data WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    PersonalData personalData = new PersonalData();
                    personalData.setName(resultSet.getString("name"));
                    personalData.setSurname(resultSet.getString("surname"));
                    personalData.setPhoneNumber(resultSet.getString("phone_number"));
                    personalData.setAvatarLink(resultSet.getString("avatar_link"));

                    return personalData;
                } else {
                    System.out.println("Personal data with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving personal data: " + e.getMessage());
        }
        return null;
    }

    public static boolean update(int id, PersonalData personalData) {
        String updateSQL = "UPDATE personal_data SET name = ?, surname = ?, phone_number = ?, avatar_link = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            preparedStatement.setString(1, personalData.getName());
            preparedStatement.setString(2, personalData.getSurname());
            preparedStatement.setString(3, personalData.getPhoneNumber());
            preparedStatement.setString(4, personalData.getAvatarLink());
            preparedStatement.setInt(5, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PersonalData successfully updated!");
                return true;
            } else {
                System.out.println("No personal data found with id " + id + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("Error while updating personal data: " + e.getMessage());
        }
        return false;
    }

    public static boolean deletePersonalDataById(int personalDataId) {
        String deletePersonalDataSQL = "DELETE FROM personal_data WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(deletePersonalDataSQL)) {

            preparedStatement.setInt(1, personalDataId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Personal data successfully deleted!");
                return true;
            } else {
                System.out.println("Personal data deletion failed. (No rows affected)");
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting personal data: " + e.getMessage());
        }

        return false;
    }
}
