package org.mccheckers.mccheckers_backend.db;

import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.model.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {
    private static final String DB_URL = Config.getDbUrl();
    private static final String DB_USER = Config.getDbUsername();
    private static final String DB_PASSWORD = Config.getDbPassword();

    public static Request create(int roomId, Timestamp timestamp, int creatorId) {
        String sqlRequest = "INSERT INTO request (room_id, date_time, moderator_id) VALUES (?, ?, NULL)";
        String sqlRequestUser = "INSERT INTO request_user (id_user, id_request) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatementRequest = connection.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatementRequest.setInt(1, roomId);
                preparedStatementRequest.setTimestamp(2, timestamp);

                int rowsAffectedRequest = preparedStatementRequest.executeUpdate();
                if (rowsAffectedRequest > 0) {
                    try (ResultSet generatedKeys = preparedStatementRequest.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int requestId = generatedKeys.getInt(1);

                            try (PreparedStatement preparedStatementRequestUser = connection.prepareStatement(sqlRequestUser)) {
                                preparedStatementRequestUser.setInt(1, creatorId);
                                preparedStatementRequestUser.setInt(2, requestId);

                                int rowsAffectedRequestUser = preparedStatementRequestUser.executeUpdate();
                                if (rowsAffectedRequestUser > 0) {
                                    connection.commit();
                                    return new Request(requestId, roomId, new Date(timestamp.getTime()), -1);
                                } else {
                                    connection.rollback();
                                    System.out.println("Request_user creation failed.");
                                }
                            }
                        }
                    }
                } else {
                    connection.rollback();
                    System.out.println("Request creation failed. (no rows affected)");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while creating request: " + e.getMessage());
        }
        return null;
    }

    public static boolean remove(int id) {
        String sqlRequest = "DELETE FROM request WHERE id = ?";
        String sqlRequestUser = "DELETE FROM request_user WHERE id_request = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatementRequestUser = connection.prepareStatement(sqlRequestUser)) {
                preparedStatementRequestUser.setInt(1, id);
                int rowsAffectedRequestUser = preparedStatementRequestUser.executeUpdate();

                try (PreparedStatement preparedStatementRequest = connection.prepareStatement(sqlRequest)) {
                    preparedStatementRequest.setInt(1, id);
                    int rowsAffectedRequest = preparedStatementRequest.executeUpdate();

                    if (/*rowsAffectedRequestUser > 0 && */rowsAffectedRequest > 0) { //todo check if this condition is needed (prly not)
                        connection.commit();
                        return true;
                    } else {
                        connection.rollback();
                        System.out.println("No rows affected, rolling back request-removing transaction.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while removing request: " + e.getMessage());
        }
        return false;
    }


    public static boolean updateModerator(int id, int moderatorId) {
        String sql = "UPDATE request SET moderator_id = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (moderatorId == 0) {
                preparedStatement.setNull(1,  java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(1, moderatorId);
            }
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Request's moderator successfully updated!");
                return true;
            } else {
                System.out.println("No request found with id " + id + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("Error while updating request's moderator: " + e.getMessage());
        }
        return false;
    }

    public static boolean addParticipant(int id, int userId) {
        String sqlRequestUser = "INSERT INTO request_user (id_user, id_request) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatementRequestUser = connection.prepareStatement(sqlRequestUser)) {
            preparedStatementRequestUser.setInt(1, userId);
            preparedStatementRequestUser.setInt(2, id);

            int rowsAffectedRequestUser = preparedStatementRequestUser.executeUpdate();
            if (rowsAffectedRequestUser > 0) {
                System.out.println("Successfully added a participant to the request " + id);
                return true;
            } else {
                System.out.println("Request_user creation failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while adding participant" + userId + " to the request: " + e.getMessage());
        }

        return false;
    }

    public static boolean removeParticipant(int id, int userId) {
        String sqlRequestUser = "DELETE FROM request_user WHERE id_request = ? AND id_user = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatementRequestUser = connection.prepareStatement(sqlRequestUser)) {
            preparedStatementRequestUser.setInt(1, id);
            preparedStatementRequestUser.setInt(2, userId);
            int rowsAffectedRequestUser = preparedStatementRequestUser.executeUpdate();
            if (rowsAffectedRequestUser > 0) {
                System.out.println("Successfully removed a participant " + userId + " from the request " + id);
                return true;
            } else {
                System.out.println("Request_user removing failed.");
            }

        } catch (SQLException e) {
            System.err.println("Error while removing participant from the request: " + e.getMessage());
        }
        return false;
    }

    public static List<Integer> getParticipants(int id) {
        List<Integer> participants = new ArrayList<>();
        String sql = "SELECT id_user from request_user WHERE id_request = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    participants.add(resultSet.getInt("id_user"));
                }
                return participants;
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving participants: " + e.getMessage());
        }

        return participants;
    }

    public static List<Request> getRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM request";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int requestId = resultSet.getInt("id");
                int roomId = resultSet.getInt("room_id");
                Timestamp timestamp = resultSet.getTimestamp("date_time");
                int moderatorId = resultSet.getInt("moderator_id");
                requests.add(new Request(requestId, roomId, new Date(timestamp.getTime()), moderatorId));
            }

        } catch (SQLException e) {
            System.err.println("Error while retrieving requests: " + e.getMessage());
        }

        return requests;
    }


    public static Request getById(int id) {
        String sql = "SELECT id, room_id, date_time, moderator_id FROM request WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Request request = new Request(
                            resultSet.getInt("id"),
                            resultSet.getInt("room_id"),
                            new Date(resultSet.getTimestamp("date_time").getTime()),
                            resultSet.getInt("moderator_id")
                    );
                    return request;
                } else {
                    System.out.println("Request with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving request: " + e.getMessage());
        }

        return null;
    }
}
