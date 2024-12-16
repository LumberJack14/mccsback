package org.mccheckers.mccheckers_backend.dto;

import jakarta.inject.Inject;
import org.mccheckers.mccheckers_backend.service.AdminService;

public class MatchResponseDTO {
    private RequestResponseDTO request;
    private boolean isSuccess;
    private UserResponseDTO winner;
    private UserResponseDTO loser;
    private int winnerScore;
    private int loserScore;
    private String scoreText; // e.g. 2:0
    private String remark;
    private UserResponseDTO moderator;
    private boolean isFriendlyMatch;

    @Inject
    private AdminService adminService;

    public MatchResponseDTO() {
    }

    public MatchResponseDTO(
            RequestResponseDTO request,
            boolean isSuccess,
            UserResponseDTO winner,
            UserResponseDTO loser,
            int winnerScore,
            int loserScore,
            String scoreText,
            String remark,
            UserResponseDTO moderator
    ) {
        this.request = request;
        this.isSuccess = isSuccess;
        this.winner = winner;
        this.loser = loser;
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.scoreText = scoreText;
        this.remark = remark;
        this.moderator = moderator;

        if (adminService.isModerator(loser.getId()) || adminService.isModerator(winner.getId())) {
            this.isFriendlyMatch = true;
        } else {
            this.isFriendlyMatch = false;
        }
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(int winnerScore) {
        this.winnerScore = winnerScore;
    }

    public int getLoserScore() {
        return loserScore;
    }

    public void setLoserScore(int loserScore) {
        this.loserScore = loserScore;
    }

    public RequestResponseDTO getRequest() {
        return request;
    }

    public void setRequest(RequestResponseDTO request) {
        this.request = request;
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
