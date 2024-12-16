package org.mccheckers.mccheckers_backend.model;

import java.util.Date;

public class Block {
    private int id;
    private int userId;
    private Date endDate;
    private String cause;

    public Block(int id, int userId, Date endDate, String cause) {
        this.id = id;
        this.userId = userId;
        this.endDate = endDate;
        this.cause = cause;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
