package org.mccheckers.mccheckers_backend.model;

public class Report {
    private int id;
    private int moderatorId;
    private int userId;
    private String reason;

    public Report(int id, int moderatorId, int userId, String reason) {
        this.id = id;
        this.moderatorId = moderatorId;
        this.userId = userId;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
