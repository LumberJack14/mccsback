package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import org.mccheckers.mccheckers_backend.db.ModeratorDAO;

@RequestScoped
public class AdminService {
    public int addModerator(int userId) {
        int id = ModeratorDAO.create(userId);
        if (id == -1) {
            throw new IllegalArgumentException("Couldn't create a moderator. Is user with this id already a moderator?");
        }
        return id;
    }

    public boolean removeModerator(int userId) {
        boolean deleted = ModeratorDAO.remove(userId);
        if (!deleted) {
            throw new IllegalArgumentException("Couldn't remove the moderator. User with the given id might not have moderator privileges already.");
        }
        return true;
    }

    public boolean isModerator(int userId) {
        return ModeratorDAO.isModerator(userId);
    }
}
