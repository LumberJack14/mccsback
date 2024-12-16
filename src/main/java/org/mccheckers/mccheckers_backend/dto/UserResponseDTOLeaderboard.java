package org.mccheckers.mccheckers_backend.dto;

import org.mccheckers.mccheckers_backend.db.UserDAO;

public class UserResponseDTOLeaderboard {
    private int id;
    private String username;
    private int elo;
    private String rank;
    private int totalMatches;

    public UserResponseDTOLeaderboard(
            int id,
            String username,
            int elo,
            String rank,
            int totalMatches
    ) {
        this.id = id;
        this.username = username;
        this.elo = elo;
        this.rank = rank;
        this.totalMatches = totalMatches;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }
}

