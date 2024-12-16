package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.mccheckers.mccheckers_backend.db.RequestDAO;
import org.mccheckers.mccheckers_backend.dto.RequestRequestDTO;
import org.mccheckers.mccheckers_backend.dto.RequestResponseDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTO;
import org.mccheckers.mccheckers_backend.model.Request;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class RequestService {

    @Inject
    private UserService userService;
    @Inject
    private AdminService adminService;

    public List<RequestResponseDTO> getCurrentRequests() {
        List<Request> requests = RequestDAO.getRequests();
        List<RequestResponseDTO> requestResponseDTOS = new ArrayList<>();

        for (Request request : requests) {
            RequestResponseDTO dto = new RequestResponseDTO();
            dto.setId(request.getId());
            dto.setDateTime(request.getDateTime());
            dto.setRoomId(request.getRoomId());

            int moderatorId = request.getModeratorId();
            if (moderatorId != 0) {
                UserResponseDTO moderator = userService.getUserById(moderatorId);
                dto.setModerator(moderator);
            }

            List<Integer> participantsIds = RequestDAO.getParticipants(request.getId());
            List<UserResponseDTO> participants = new ArrayList<>();
            for (Integer id : participantsIds) {
                participants.add(userService.getUserById(id));
            }
            dto.setParticipants(participants);

            requestResponseDTOS.add(dto);
        }

        return requestResponseDTOS;
    }

    public Request createRequest(RequestRequestDTO dto, int userId) {
        return RequestDAO.create(dto.getRoomId(), new Timestamp(dto.getDateTime().getTime()), userId);
    }

    public boolean subscribeToRequest(int requestId, String username) throws IllegalArgumentException {
        Request request = RequestDAO.getById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request with id " + requestId + " doesn't exist.");
        };

        if (RequestDAO.getParticipants(requestId).size() >= 2) {
            throw new IllegalArgumentException("Request with id " + requestId + " is full.");
        }

        int userId = userService.getIdByUsername(username);

        if (adminService.isModerator(userId)) {
            RequestDAO.updateModerator(requestId, userId);
        } else {
            RequestDAO.addParticipant(requestId, userId);
        }
        return true;
    }

    public boolean unsubscribeFromRequest(int requestId, String username) throws IllegalArgumentException {
        Request request = RequestDAO.getById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request with id " + requestId + " doesn't exist.");
        }
        int userId = userService.getIdByUsername(username);
        List<Integer> participants = RequestDAO.getParticipants(requestId);

        if (!participants.contains(userId)) {
            throw new IllegalArgumentException("User with id " + userId + " is not a participant of this request.");
        }

        if (adminService.isModerator(userId)) {
            if (participants.isEmpty()) {
                RequestDAO.remove(requestId);
                return true;
            }
            RequestDAO.updateModerator(requestId, 0);
        } else {
            if (request.getModeratorId() == 0 && participants.size() <= 1) {
                RequestDAO.remove(requestId);
                return true;
            }
            RequestDAO.removeParticipant(requestId, userId);
        }


        return true;
    }

    public RequestResponseDTO getRequestById(int id) throws IllegalArgumentException {
        Request request = RequestDAO.getById(id);
        if (request == null) {
            throw new IllegalArgumentException("No request found with id " + id);
        }
        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(id);
        dto.setDateTime(request.getDateTime());
        dto.setRoomId(request.getRoomId());
        int moderatorId = request.getModeratorId();
        if (moderatorId != 0) {
            UserResponseDTO moderator = userService.getUserById(moderatorId);
            dto.setModerator(moderator);
        }
        List<Integer> participantsIds = RequestDAO.getParticipants(request.getId());
        List<UserResponseDTO> participants = new ArrayList<>();
        for (Integer i : participantsIds) {
            participants.add(userService.getUserById(i));
        }
        dto.setParticipants(participants);

        return dto;
    }
}
