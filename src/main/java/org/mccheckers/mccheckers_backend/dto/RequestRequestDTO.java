package org.mccheckers.mccheckers_backend.dto;

import java.util.Date;

public class RequestRequestDTO {
    private int roomId;
    private Date dateTime;
    //private int creatorId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /*public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }*/
}
