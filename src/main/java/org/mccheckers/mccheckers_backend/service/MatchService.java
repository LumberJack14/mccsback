package org.mccheckers.mccheckers_backend.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.mccheckers.mccheckers_backend.db.MatchDAO;
import org.mccheckers.mccheckers_backend.db.UserDAO;
import org.mccheckers.mccheckers_backend.dto.MatchRequestDTO;
import org.mccheckers.mccheckers_backend.dto.MatchResponseDTO;
import org.mccheckers.mccheckers_backend.dto.UserRequestDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTO;
import org.mccheckers.mccheckers_backend.model.Match;
import org.mccheckers.mccheckers_backend.model.User;
import org.mccheckers.mccheckers_backend.util.EloCalculator;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class MatchService {

    @Inject
    private RequestService requestService;
    @Inject
    private UserService userService;

    public MatchResponseDTO createMatch(MatchRequestDTO dto) throws Exception {
        int scoreId = getScoreId(dto.getWinnerScore(), dto.getLoserScore());

        Match match = MatchDAO.create(
                dto.getRequestId(),
                dto.isSuccess(),
                dto.getWinnerId(),
                dto.getLoserId(),
                dto.getRemark(),
                dto.getModeratorId(),
                scoreId
        );

        if (match == null) {
            throw new Exception("Server error while creating match");
        }

        UserResponseDTO winner = userService.getUserById(match.getWinnerId());
        UserResponseDTO loser = userService.getUserById(match.getLoserId());

        if (match.isSuccess()) {
            int[] newElo = EloCalculator.calculateElo(winner.getElo(), loser.getElo(), 1.0);
            UserDAO.updateElo(winner.getId(), newElo[0]);
            UserDAO.updateElo(loser.getId(), newElo[1]);
        }

        MatchResponseDTO matchResponseDTO = new MatchResponseDTO(
                requestService.getRequestById(match.getRequestId()),
                match.isSuccess(),
                userService.getUserById(match.getWinnerId()),
                userService.getUserById(match.getLoserId()),
                dto.getWinnerScore(),
                dto.getLoserScore(),
                getScoreText(match.getScoreId()),
                match.getRemark(),
                userService.getUserById(match.getModeratorId())
        );

        return matchResponseDTO;
    }

    public MatchResponseDTO getMatchById(int id) {
        Match match = MatchDAO.getById(id);
        if (match == null) {
            throw new IllegalArgumentException("No match found with id " + id);
        }
        return new MatchResponseDTO(
                requestService.getRequestById(match.getRequestId()),
                match.isSuccess(),
                userService.getUserById(match.getWinnerId()),
                userService.getUserById(match.getLoserId()),
                Character.getNumericValue((getScoreText(match.getScoreId()).charAt(0))),
                Character.getNumericValue((getScoreText(match.getScoreId()).charAt(2))),
                getScoreText(match.getScoreId()),
                match.getRemark(),
                userService.getUserById(match.getModeratorId())
        );
    }

    public List<MatchResponseDTO> getAllMatches() {
        List<Match> matches = MatchDAO.getAllMatches();
        List<MatchResponseDTO> matchResponseDTOS = new ArrayList<>();

        for (Match match: matches) {
            MatchResponseDTO matchResponseDTO = new MatchResponseDTO(
                    requestService.getRequestById(match.getRequestId()),
                    match.isSuccess(),
                    userService.getUserById(match.getWinnerId()),
                    userService.getUserById(match.getLoserId()),
                    Character.getNumericValue((getScoreText(match.getScoreId()).charAt(0))),
                    Character.getNumericValue((getScoreText(match.getScoreId()).charAt(2))),
                    getScoreText(match.getScoreId()),
                    match.getRemark(),
                    userService.getUserById(match.getModeratorId())
            );
            matchResponseDTOS.add(matchResponseDTO);
        }

        return matchResponseDTOS;
    }

    public List<MatchResponseDTO> getMatchesUser(int userId) {
        List<Match> matches = MatchDAO.getMatchesUser(userId);
        List<MatchResponseDTO> matchResponseDTOS = new ArrayList<>();
        for (Match match: matches) {
            MatchResponseDTO matchResponseDTO = new MatchResponseDTO(
                    requestService.getRequestById(match.getRequestId()),
                    match.isSuccess(),
                    userService.getUserById(match.getWinnerId()),
                    userService.getUserById(match.getLoserId()),
                    Character.getNumericValue((getScoreText(match.getScoreId()).charAt(0))),
                    Character.getNumericValue((getScoreText(match.getScoreId()).charAt(2))),
                    getScoreText(match.getScoreId()),
                    match.getRemark(),
                    userService.getUserById(match.getModeratorId())
            );
            matchResponseDTOS.add(matchResponseDTO);
        }

        return matchResponseDTOS;
    }


    // 1: 1:0
    // 2: 2:0
    // 3: 2:1
    // 4: 0:0

    private int getScoreId(int winnerScore, int loserScore) {
        if (winnerScore == 0) return 4;

        if (winnerScore == 2) {
            if (loserScore == 0) return 2;
            else return 3;
        } else return 1;
    }

    private String getScoreText(int scoreId){
        switch (scoreId) {
            case 1: return "1:0";
            case 2: return "2:0";
            case 3: return "2:1";
            default: return "0:0";
        }
    }
}
