package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.exception.ValidationException;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.service.PlayerService;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(NewMatchController.class);
    private final PlayerService playerService = new PlayerService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String playerOneName = request.getParameter("playerOneName");
            String playerTwoName = request.getParameter("playerTwoName");
            log.info("Received request to create new match: player1='{}', player2='{}'", playerOneName, playerTwoName);
            Validator.validateName(playerOneName);
            Validator.validateName(playerTwoName);

            if (playerOneName.equals(playerTwoName)) {
                throw new ValidationException("Player names must be different");
            }

            Player p1 = playerService.findOrCreatePlayer(playerOneName);
            Player p2 = playerService.findOrCreatePlayer(playerTwoName);
            UUID matchId = OngoingMatchesService.createMatch(p1, p2);
            log.info("Match created with id {}", matchId);
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + matchId);
        } catch (ValidationException e) {
            log.warn("Validation error while creating match: {}", e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp").forward(request, response);
        }
    }
}