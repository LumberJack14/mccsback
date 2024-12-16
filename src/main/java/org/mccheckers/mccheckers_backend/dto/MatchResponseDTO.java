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

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public UserResponseDTO getWinner() {
        return winner;
    }

    public void setWinner(UserResponseDTO winner) {
        this.winner = winner;
    }

    public UserResponseDTO getLoser() {
        return loser;
    }

    public void setLoser(UserResponseDTO loser) {
        this.loser = loser;
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public String getScoreText() {
        return scoreText;
    }

    public void setScoreText(String scoreText) {
        this.scoreText = scoreText;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserResponseDTO getModerator() {
        return moderator;
    }

    public void setModerator(UserResponseDTO moderator) {
        this.moderator = moderator;
    }
}
