package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.mccheckers.mccheckers_backend.db.ModeratorDAO;
import org.mccheckers.mccheckers_backend.db.PersonalDataDAO;
import org.mccheckers.mccheckers_backend.db.UserDAO;
import org.mccheckers.mccheckers_backend.dto.UserRequestDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTOLeaderboard;
import org.mccheckers.mccheckers_backend.model.PersonalData;
import org.mccheckers.mccheckers_backend.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mccheckers.mccheckers_backend.security.BCryptHashing.hashPassword;
import static org.mccheckers.mccheckers_backend.security.BCryptHashing.verifyPassword;

@RequestScoped
public class UserService {

    @Inject
    private AdminService adminService;

    public int registerUser(UserRequestDTO userDTO) throws IllegalArgumentException {
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
            return new UserResponseDTO(
                    userInstance.getId(),
                    userInstance.getUsername(),
                    userInstance.getElo(),
                    userInstance.isActive(),
                    personalDataInstance.getName(),
                    personalDataInstance.getSurname(),
                    personalDataInstance.getPhoneNumber(),
                    personalDataInstance.getAvatarLink(),
                    ModeratorDAO.isModerator(id)
                    );
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
                    personalDataToUpdate.getAvatarLink(),
                    ModeratorDAO.isModerator(userToUpdate.getId())
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

    public List<UserResponseDTO> findUsersBySubstring(String str) {
        List<User> users = UserDAO.findUsersByUsernameSubstring(str);
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
        for (User user: users) {
            PersonalData pd = PersonalDataDAO.getPersonalDataById(user.getId()); //trust that personal data exists since
            UserResponseDTO dto = new UserResponseDTO(                           //user creation is atomic (kind of)
                    user.getId(),
                    user.getUsername(),
                    user.getElo(),
                    user.isActive(),
                    pd.getName(),
                    pd.getSurname(),
                    pd.getPhoneNumber(),
                    pd.getAvatarLink(),
                    ModeratorDAO.isModerator(user.getId())
            );
            userResponseDTOS.add(dto);
        }
        return userResponseDTOS;
    }

    public List<UserResponseDTOLeaderboard> getLeaderboard(int limit) {
        return UserDAO.getLeaderboard(limit);
    }

    public List<UserResponseDTO> getInactiveUsers() {
        List<User> users = UserDAO.getInactiveUsers();
        List<UserResponseDTO> dtos = new ArrayList<>();
        for (User user: users) {
            PersonalData pd = PersonalDataDAO.getPersonalDataById(user.getId());
            dtos.add(new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getElo(),
                    user.isActive(),
                    pd.getName(),
                    pd.getSurname(),
                    pd.getPhoneNumber(),
                    pd.getAvatarLink(),
                    ModeratorDAO.isModerator(user.getId())
                    ));
        }
            return dtos;
    }
}

