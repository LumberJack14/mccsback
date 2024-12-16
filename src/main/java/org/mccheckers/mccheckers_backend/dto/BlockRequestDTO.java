package org.mccheckers.mccheckers_backend.dto;

import java.util.Date;

public class BlockRequestDTO {
    int userId;
    Date endDate;
    String cause;

    public BlockRequestDTO( int userId, Date endDate, String cause) {
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
