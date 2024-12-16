package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.PersonalData;
import org.mccheckers.mccheckers_backend.model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static Report createReport(int moderatorId, String reason, int userId) {
        String insertUserSQL = "INSERT INTO report (moderator_id, reason, _user_id) VALUES (?, ?, ?)";


        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, moderatorId);
            preparedStatement.setString(2, reason);
            preparedStatement.setInt(3, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("report successfully created!");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new Report(
                                generatedKeys.getInt(1),
                                moderatorId,
                                userId,
                                reason
                        );
                    }
                }
            } else {
                System.out.println("report creation failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while creating report: " + e.getMessage());
        }
        return null;
    }

    public static Report getById(int id) {
        String selectSQL = "SELECT id, moderator_id, reason, _user_id FROM report WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Report(
                            id,
                            resultSet.getInt("moderator_id"),
                            resultSet.getInt("_user_id"),
                            resultSet.getString("reason")
                    );
                } else {
                    System.out.println("report with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving report: " + e.getMessage());
        }
        return null;
    }

    public static List<Report> getReportsUser(int userId) {
        String selectSQL = "SELECT id, moderator_id, reason, _user_id FROM report WHERE _user_id = ? OR moderator_id = ?";
        List<Report> reports = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(new Report(
                            resultSet.getInt("id"),
                            resultSet.getInt("moderator_id"),
                            resultSet.getInt("_user_id"),
                            resultSet.getString("reason")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving reports for user: " + e.getMessage());
        }
        return reports;
    }

    public static List<Report> getReportsByModerator(int moderatorId) {
        String selectSQL = "SELECT id, moderator_id, reason, _user_id FROM report WHERE moderator_id = ?";
        List<Report> reports = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, moderatorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(new Report(
                            resultSet.getInt("id"),
                            resultSet.getInt("moderator_id"),
                            resultSet.getInt("_user_id"),
                            resultSet.getString("reason")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving reports for moderator: " + e.getMessage());
        }
        return reports;
    }

    public static List<Report> getAllReports() {
        String selectSQL = "SELECT id, moderator_id, reason, _user_id FROM report";
        List<Report> reports = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(new Report(
                            resultSet.getInt("id"),
                            resultSet.getInt("moderator_id"),
                            resultSet.getInt("_user_id"),
                            resultSet.getString("reason")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving reports for moderator: " + e.getMessage());
        }
        return reports;
    }
}
