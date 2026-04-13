package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.service.PlayerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchController extends HttpServlet {

    private final PlayerService playerService = new PlayerService();

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

        Player p1 = playerService.findOrCreatePlayer(playerOneName);
        Player p2 = playerService.findOrCreatePlayer(playerTwoName);
        UUID matchId = OngoingMatchesService.createMatch(p1, p2);
        response.sendRedirect(request.getContextPath() + "/match-score?id=" + matchId);
    }
}