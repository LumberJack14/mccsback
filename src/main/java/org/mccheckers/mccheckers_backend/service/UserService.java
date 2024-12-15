package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import org.mccheckers.mccheckers_backend.db.PersonalDataDAO;
import org.mccheckers.mccheckers_backend.db.UserDAO;
import org.mccheckers.mccheckers_backend.dto.UserRequestDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTO;
import org.mccheckers.mccheckers_backend.model.PersonalData;
import org.mccheckers.mccheckers_backend.model.User;

import java.util.Optional;

import static org.mccheckers.mccheckers_backend.security.BCryptHashing.hashPassword;
import static org.mccheckers.mccheckers_backend.security.BCryptHashing.verifyPassword;

@RequestScoped
public class UserService {

    public int registerUser(UserRequestDTO userDTO) {
        if (isUsernameTaken(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        String hashedPassword = hashPassword(userDTO.getPassword());
        int id = UserDAO.createUser(
                userDTO.getUsername(),
                100,
                hashedPassword,
                userDTO.getName(),
                userDTO.getSurname(),
                userDTO.getPhoneNumber(),
                userDTO.getCountryId()
        );

        return id;
    }

    public boolean comparePasswords(String username, String password) {
        User user = UserDAO.findByUsername(username);
        return verifyPassword(password, user.getPasswordHash());
    }

    // Check if email already exists in the database
    public boolean isUsernameTaken(String username) {
        Optional<User> user = Optional.ofNullable(UserDAO.findByUsername(username));
        return user.isPresent();
    }

    public UserResponseDTO getUserById(int id) {
        Optional<User> user = Optional.ofNullable(UserDAO.findById(id));
        Optional<PersonalData> personalData = Optional.ofNullable(PersonalDataDAO.getPersonalDataById(id));
        if (user.isPresent() && personalData.isPresent()) {
            User userInstance = user.get();
            PersonalData personalDataInstance = personalData.get();
            UserResponseDTO userResponseDTO = new UserResponseDTO(
                    userInstance.getId(),
                    userInstance.getUsername(),
                    userInstance.getElo(),
                    userInstance.isActive(),
                    personalDataInstance.getName(),
                    personalDataInstance.getSurname(),
                    personalDataInstance.getPhoneNumber(),
                    personalDataInstance.getAvatarLink()
                    );
            return userResponseDTO;
        }
        return null;
    }

    public int getIdByUsername(String username) {
        User user = UserDAO.findByUsername(username);
        if (user == null) {
            return -1;
        } else {
            return user.getId();
        }
    }

    public UserResponseDTO updateUser(int id, UserRequestDTO userRequestDTO) {
        Optional<User> existingUser = Optional.ofNullable(UserDAO.findById(id));
        Optional<PersonalData> existingPersonalData = Optional.ofNullable(PersonalDataDAO.getPersonalDataById(id));

        if (existingUser.isPresent() && existingPersonalData.isPresent()) {
            User userToUpdate = existingUser.get();
            PersonalData personalDataToUpdate = existingPersonalData.get();

            if (userRequestDTO.getUsername() != null) {
                userToUpdate.setUsername(userRequestDTO.getUsername());
            }


            UserDAO.update(userToUpdate);

            if (userRequestDTO.getName() != null) {
                personalDataToUpdate.setName(userRequestDTO.getName());
            }
            if (userRequestDTO.getSurname() != null) {
                personalDataToUpdate.setSurname(userRequestDTO.getSurname());
            }
            if (userRequestDTO.getPhoneNumber() != null) {
                personalDataToUpdate.setPhoneNumber(userRequestDTO.getPhoneNumber());
            }
            if (userRequestDTO.getAvatarLink() != null) {
                personalDataToUpdate.setAvatarLink(userRequestDTO.getAvatarLink());
            }

            PersonalDataDAO.update(id, personalDataToUpdate);

            return new UserResponseDTO(
                    userToUpdate.getId(),
                    userToUpdate.getUsername(),
                    userToUpdate.getElo(),
                    userToUpdate.isActive(),
                    personalDataToUpdate.getName(),
                    personalDataToUpdate.getSurname(),
                    personalDataToUpdate.getPhoneNumber(),
                    personalDataToUpdate.getAvatarLink()
            );
        }

        System.err.println("User with ID " + id + " not found.");
        return null;
    }

    public boolean deleteUser(int id) {
        //necessary validations for checking, who we delete?
        User user = UserDAO.findById(id);
        if (user != null) {
            return PersonalDataDAO.deletePersonalDataById(id) && UserDAO.deleteUserById(id);
        } else {
            System.out.println("User with id " + id + " not found.");
            return false;
        }
    }


}

