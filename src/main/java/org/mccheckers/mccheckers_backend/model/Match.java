package org.mccheckers.mccheckers_backend.model;

public class Match {
    private int id;
    private int requestId;
    private boolean isSuccess;
    private int winnerId;
    private int loserId;
    private int scoreId;
    private String remark;
    private int moderatorId;

    public Match(
            int id,
            int requestId,
            boolean isSuccess,
            int winnerId,
            int loserId,
            int scoreId,
            String remark,
            int moderatorId
    ) {
        this.id = id;
        this.requestId = requestId;
        this.isSuccess = isSuccess;
        this.winnerId = winnerId;
        this.loserId = loserId;
        this.scoreId = scoreId;
        this.remark = remark;
        this.moderatorId = moderatorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getLoserId() {
        return loserId;
    }

    public void setLoserId(int loserId) {
        this.loserId = loserId;
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }
}
