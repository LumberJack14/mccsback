package org.mccheckers.mccheckers_backend.model;

import java.util.Date;

public class Request {
    private int id;
    private int roomId;
    private Date dateTime;
    private int moderatorId;

    public Request(int id, int roomId, Date dateTime, int moderatorId) {
        this.id = id;
        this.roomId = roomId;
        this.dateTime = dateTime;
        this.moderatorId = moderatorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }
}
