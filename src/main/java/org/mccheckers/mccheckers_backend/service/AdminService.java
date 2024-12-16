package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import org.mccheckers.mccheckers_backend.db.ModeratorDAO;
import org.mccheckers.mccheckers_backend.db.UserDAO;
import org.mccheckers.mccheckers_backend.model.User;

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

    public boolean activateUser(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("No user with id " + userId + " found.");
        }

        if (user.isActive()) {
            throw new IllegalArgumentException("User is already active.");
        }

        user.setActive(true);
        if (!UserDAO.update(user)) {
            throw new IllegalArgumentException("Couldn't update the user in database.");
        }

        return true;
    }

    public boolean deactivateUser(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("No user with id " + userId + " found.");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("User is already inactive.");
        }

        user.setActive(false);
        if (!UserDAO.update(user)) {
            throw new IllegalArgumentException("Couldn't update the user in database.");
        }

        return true;
    }


}
