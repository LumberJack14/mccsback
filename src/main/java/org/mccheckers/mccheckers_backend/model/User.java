package org.mccheckers.mccheckers_backend.model;

public class User {
    private int id;
    private String username;
    private int elo;
    private String passwordHash;
    private boolean active;

    public User() {

    }

    public User(
            int id,
            String username,
            int elo,
            String passwordHash,
            boolean active
    ) {
        this.id = id;
        this.username = username;
        this.elo = elo;
        this.passwordHash = passwordHash;
        this.active = active;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
