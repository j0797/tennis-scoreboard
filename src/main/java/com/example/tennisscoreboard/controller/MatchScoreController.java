package com.example.tennisscoreboard.controller;

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
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            UUID matchId = UUID.fromString(idParam);
            OngoingMatch ongoingMatch = OngoingMatchesService.getMatch(matchId);
            if (ongoingMatch == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("match", ongoingMatch);
            request.setAttribute("score", ongoingMatch.getScore());
            request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String playerParam = request.getParameter("player");

        if (idParam == null || playerParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UUID matchId = UUID.fromString(idParam);
            int playerNumber = Integer.parseInt(playerParam);
            OngoingMatch match = OngoingMatchesService.getMatch(matchId);
            OngoingMatchesService.addPoint(matchId, playerNumber);
            if (match != null && match.isMatchOver()) {
                response.sendRedirect(request.getContextPath() + "/matches");
            } else {
                response.sendRedirect(request.getContextPath() + "/match-score?id=" + matchId);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID");
        }
    }
}