package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.config.ApplicationContext;
import com.example.tennisscoreboard.exception.ValidationException;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletConfig;
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
    private static final String PARAM_PLAYER_ONE = "playerOneName";
    private static final String PARAM_PLAYER_TWO = "playerTwoName";
    private static final String ATTR_ERROR = "error";
    private static final String VIEW_NEW_MATCH = "/WEB-INF/jsp/new-match.jsp";
    private static final String VALIDATION_ERROR_MESSAGE = "Name: unique; 2–30 chars, no outer spaces. Allowed: Ru/En letters, inner spaces, hyphens, apostrophes.";
    private MatchService matchService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("appContext");
        this.matchService = context.getMatchService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher(VIEW_NEW_MATCH).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String playerOneName = request.getParameter(PARAM_PLAYER_ONE);
            String playerTwoName = request.getParameter(PARAM_PLAYER_TWO);
            log.info("Received request to create new match: player1='{}', player2='{}'", playerOneName, playerTwoName);
            Validator.validateNames(playerOneName, playerTwoName);
            UUID matchId = matchService.createMatch(playerOneName, playerTwoName);
            log.info("Match created with id {}", matchId);
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + matchId);
        } catch (ValidationException e) {
            log.warn("Validation error while creating match: {}", e.getMessage());
            request.setAttribute(ATTR_ERROR, VALIDATION_ERROR_MESSAGE);
            request.getRequestDispatcher(VIEW_NEW_MATCH).forward(request, response);
        }
    }
}