package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.config.ApplicationContext;
import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletConfig;
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
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_PLAYER = "player";
    private static final String ATTR_DISPLAY_DTO = "displayDto";
    private static final String ATTR_MATCH_OVER = "matchOver";
    private static final String VIEW_MATCH_SCORE = "/WEB-INF/jsp/match-score.jsp";
    private MatchService matchService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("appContext");
        this.matchService = context.getMatchService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = Validator.parseUuid(request.getParameter(PARAM_UUID));
        log.debug("GET match-score with uuid={}", id);
        ScoreDto displayDto = matchService.getMatchScore(id);
        request.setAttribute(ATTR_DISPLAY_DTO, displayDto);
        request.getRequestDispatcher(VIEW_MATCH_SCORE).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UUID id = Validator.parseUuid(request.getParameter(PARAM_UUID));
        int playerNumber = Validator.parsePlayerNumber(request.getParameter(PARAM_PLAYER));
        log.info("POST match-score: match={}, player={}", id, playerNumber);

        ScoreDto displayDto = matchService.addPoint(id, playerNumber);

        if (displayDto != null) {
            log.info("Match {} finished after point by player {}", id, playerNumber);
            request.setAttribute(ATTR_DISPLAY_DTO, displayDto);
            request.setAttribute(ATTR_MATCH_OVER, true);
            request.getRequestDispatcher(VIEW_MATCH_SCORE).forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + id);
        }
    }
}