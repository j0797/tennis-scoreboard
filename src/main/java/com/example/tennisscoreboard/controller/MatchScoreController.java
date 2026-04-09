package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.model.MatchScore;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {

    private final MatchService matchService = new MatchService();
    private final OngoingMatchesService ongoingMatches = new OngoingMatchesService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            Long matchId = Long.parseLong(idParam);
            Match match = matchService.getMatch(matchId);
            MatchScore score = ongoingMatches.getScore(matchId);
            request.setAttribute("match", match);
            request.setAttribute("score", score);
            request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid match id");
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
            long matchId = Long.parseLong(idParam);
            int playerNumber = Integer.parseInt(playerParam);
            ongoingMatches.addPoint(matchId, playerNumber);
            response.sendRedirect(request.getContextPath() + "/match-score?id=" + matchId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
        }
    }
}