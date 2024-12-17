package org.mccheckers.mccheckers_backend.dto;

import org.mccheckers.mccheckers_backend.db.UserDAO;

public class UserResponseDTO {
    private int id;
    private String username;
    private int elo;
    private boolean active;
    private String name;
    private String surname;
    private String phoneNumber;
    private String avatarLink;
    private boolean isModerator;
    private String rank;

    public UserResponseDTO(int id, String username, int elo, boolean active, String name, String surname, String phoneNumber, String avatarLink, boolean isModerator) {
        this.id = id;
        this.username = username;
        this.elo = elo;
        this.active = active;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.avatarLink = avatarLink;
        this.isModerator = isModerator;
        this.rank = UserDAO.getUserRank(id);
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}
