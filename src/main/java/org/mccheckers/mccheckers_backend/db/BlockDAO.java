package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.Block;
import org.mccheckers.mccheckers_backend.model.PersonalData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static int createBlock(int userId, java.util.Date endDate, String cause) {
        String insertUserSQL = "INSERT INTO block_list (_user_id, end_date, cause) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setTimestamp(2, new Timestamp(endDate.getTime()));
            preparedStatement.setString(3, cause);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("block successfully created!");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            } else {
                System.out.println("block creation failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while creating PersonalData: " + e.getMessage());
        }
        return -1;
    }

    public static List<Block> getBlocksUser(int userId) {
        String selectSQL = "SELECT id, _user_id, end_date, cause FROM block_list WHERE _user_id = ?";
        List<Block> blocks = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    blocks.add(new Block
                            (
                                    resultSet.getInt("id"),
                                    resultSet.getInt("_user_id"),
                                    new Date(resultSet.getTimestamp("end_date").getTime()),
                                    resultSet.getString("cause")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving personal data: " + e.getMessage());
        }
        return blocks;
    }
}
