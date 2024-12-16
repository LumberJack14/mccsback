package org.mccheckers.mccheckers_backend.dto;

public class MatchResponseDTO {
    int requestId;
    boolean isSuccess;
    UserResponseDTO winner;
    UserResponseDTO loser;
    int scoreId;
    String scoreText;
    String remark;
    UserResponseDTO moderator;
}
