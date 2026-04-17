package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchScoreDisplayDto;
import com.example.tennisscoreboard.mapper.MatchScoreDisplayMapper;
import com.example.tennisscoreboard.model.OngoingMatch;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String matchId = request.getParameter("id");
        if (matchId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            UUID id = UUID.fromString(matchId);
            OngoingMatch ongoingMatch = OngoingMatchesService.getOngoingMatch(id);
            if (ongoingMatch == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            MatchScoreDisplayDto displayDto = MatchScoreDisplayMapper.toDisplayDto(ongoingMatch);
            request.setAttribute("displayDto", displayDto);
            request.setAttribute("match", ongoingMatch);

            request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String matchId = request.getParameter("id");
        String playerNumber = request.getParameter("player");

        if (matchId == null || playerNumber == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UUID id = UUID.fromString(matchId);
            int player = Integer.parseInt(playerNumber);
            OngoingMatch match = OngoingMatchesService.getOngoingMatch(id);

            if (match == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            OngoingMatchesService.addPoint(id, player);

            if (match.isMatchOver()) {
                response.sendRedirect(request.getContextPath() + "/matches");
            } else {
                response.sendRedirect(request.getContextPath() + "/match-score?id=" + matchId);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID or player number");
        }
    }
}