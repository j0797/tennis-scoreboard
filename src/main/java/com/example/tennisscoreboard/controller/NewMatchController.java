package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.service.MatchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchController extends HttpServlet {

    private final MatchService matchService = new MatchService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String playerOneName = request.getParameter("playerOneName");
        String playerTwoName = request.getParameter("playerTwoName");

        if (playerOneName == null || playerOneName.isBlank() ||
                playerTwoName == null || playerTwoName.isBlank() ||
                playerOneName.equals(playerTwoName)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player names");
            return;
        }

        UUID matchId = matchService.createOngoingMatch(playerOneName, playerTwoName);
        response.sendRedirect(request.getContextPath() + "/match-score?id=" + matchId);
    }
}