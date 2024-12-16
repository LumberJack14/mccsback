package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static Match create(int requestId, boolean isSuccess, int winnerId, int loserId, String remark, int moderatorId, int scoreId) {
        String sqlMatch = "INSERT INTO _match (request_id, issuccess, winner_id, loser_id, remark, moderator_id, score_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlMatchUser = "INSERT INTO match_user (id_user, id_match) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatementMatch = connection.prepareStatement(sqlMatch, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatementMatch.setInt(1, requestId);
                preparedStatementMatch.setBoolean(2, isSuccess);
                preparedStatementMatch.setInt(3, winnerId);
                preparedStatementMatch.setInt(4, loserId);
                preparedStatementMatch.setString(5, remark);
                preparedStatementMatch.setInt(6, moderatorId);
                preparedStatementMatch.setInt(7, scoreId);

                int rowsAffectedMatch = preparedStatementMatch.executeUpdate();
                if (rowsAffectedMatch > 0) {
                    try (ResultSet generatedKeys = preparedStatementMatch.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int matchId = generatedKeys.getInt(1);

                            try (PreparedStatement preparedStatementMatchUser = connection.prepareStatement(sqlMatchUser)) {
                                preparedStatementMatchUser.setInt(1, winnerId);
                                preparedStatementMatchUser.setInt(2, matchId);

                                int rowsAffectedMatchUser = preparedStatementMatchUser.executeUpdate();
                                if (rowsAffectedMatchUser <= 0) {
                                    connection.rollback();
                                    return null;
                                }
                            }

                            try (PreparedStatement preparedStatementMatchUser = connection.prepareStatement(sqlMatchUser)) {
                                preparedStatementMatchUser.setInt(1, loserId);
                                preparedStatementMatchUser.setInt(2, matchId);

                                int rowsAffectedMatchUser = preparedStatementMatchUser.executeUpdate();
                                if (rowsAffectedMatchUser <= 0) {
                                    connection.rollback();
                                    return null;
                                } else {
                                    connection.commit();
                                    return new Match(matchId, requestId, isSuccess, winnerId, loserId, scoreId, remark, moderatorId);
                                }
                            }
                        }
                    }
                } else {
                    connection.rollback();
                    System.out.println("Match creation failed. (no rows affected)");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while creating match: " + e.getMessage());
        }
        return null;
    }


    public static Match getById(int id) {
        String sql = "SELECT * FROM _match WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Match match = new Match(
                        resultSet.getInt("id"),
                        resultSet.getInt("request_id"),
                        resultSet.getBoolean("issuccess"),
                        resultSet.getInt("winner_id"),
                        resultSet.getInt("loser_id"),
                        resultSet.getInt("score_id"),
                        resultSet.getString("remark"),
                        resultSet.getInt("moderator_id")
                );

                return match;
            } else {
                System.out.println("No Match found with id " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving match by id: " + e.getMessage());
        }
        return null;
    }

    public static List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM _match";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Match match = new Match(
                        resultSet.getInt("id"),
                        resultSet.getInt("request_id"),
                        resultSet.getBoolean("issuccess"),
                        resultSet.getInt("winner_id"),
                        resultSet.getInt("loser_id"),
                        resultSet.getInt("score_id"),
                        resultSet.getString("remark"),
                        resultSet.getInt("moderator_id")
                );

                matches.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving all matches: " + e.getMessage());
        }
        return matches;
    }

    public static List<Match> getMatchesUser(int userId) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM _match WHERE winner_id = ? OR loser_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Match match = new Match(
                        resultSet.getInt("id"),
                        resultSet.getInt("request_id"),
                        resultSet.getBoolean("issuccess"),
                        resultSet.getInt("winner_id"),
                        resultSet.getInt("loser_id"),
                        resultSet.getInt("score_id"),
                        resultSet.getString("remark"),
                        resultSet.getInt("moderator_id")
                );

                matches.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving all matches: " + e.getMessage());
        }
        return matches;
    }
}
