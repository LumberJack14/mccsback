package org.mccheckers.mccheckers_backend.dto;

import java.util.Date;
import java.util.List;

public class RequestResponseDTO {
    private int id;
    private int roomId;
    private Date dateTime;
    private UserResponseDTO moderator;
    private List<UserResponseDTO> participants;

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

    public UserResponseDTO getModerator() {
        return moderator;
    }

    public void setModerator(UserResponseDTO moderator) {
        this.moderator = moderator;
    }

    public List<UserResponseDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserResponseDTO> participants) {
        this.participants = participants;
    }
}
