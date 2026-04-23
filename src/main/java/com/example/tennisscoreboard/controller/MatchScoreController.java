package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchScoreDisplayDto;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.mapper.MatchScoreDisplayMapper;
import com.example.tennisscoreboard.model.OngoingMatch;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MatchScoreController.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String matchIdParam = request.getParameter("uuid");
        log.debug("GET match-score with uuid={}", matchIdParam);
        UUID id = Validator.validateUuid(matchIdParam);
        OngoingMatch ongoingMatch = OngoingMatchesService.getOngoingMatch(id);
        if (ongoingMatch == null) {
            throw new NotFoundException("Match not found");
        }
        MatchScoreDisplayDto displayDto = MatchScoreDisplayMapper.toDisplayDto(ongoingMatch);
        request.setAttribute("displayDto", displayDto);
        request.setAttribute("match", ongoingMatch);
        request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String matchIdParam = request.getParameter("uuid");
        String playerNumberParam = request.getParameter("player");
        log.info("POST match-score: match={}, player={}", matchIdParam, playerNumberParam);
        UUID id = Validator.validateUuid(matchIdParam);
        int player = Validator.validatePlayerNumber(playerNumberParam);

        OngoingMatch match = OngoingMatchesService.getOngoingMatch(id);
        if (match == null) {
            log.warn("Match not found for uuid {}", id);
            throw new NotFoundException("Match not found");
        }

        OngoingMatchesService.addPoint(id, player);

        if (match.isMatchOver()) {
            log.info("Match {} finished after point by player {}", id, player);
            MatchScoreDisplayDto displayDto = MatchScoreDisplayMapper.toDisplayDto(match);
            request.setAttribute("displayDto", displayDto);
            request.setAttribute("match", match);
            request.setAttribute("matchOver", true);
            request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + id);
        }
    }
}