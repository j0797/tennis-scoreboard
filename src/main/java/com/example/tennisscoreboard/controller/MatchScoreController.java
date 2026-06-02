package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.service.PlayerService;
import com.example.tennisscoreboard.service.impl.FinishedMatchesPersistenceServiceImpl;
import com.example.tennisscoreboard.service.impl.MatchServiceImpl;
import com.example.tennisscoreboard.service.impl.OngoingMatchesServiceImpl;
import com.example.tennisscoreboard.service.impl.PlayerServiceImpl;
import com.example.tennisscoreboard.util.HibernateUtil;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
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
    public void init() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PlayerDao playerDao = new PlayerDao(sessionFactory);
        MatchDao matchDao = new MatchDao(sessionFactory);
        PlayerService playerService = new PlayerServiceImpl(playerDao);
        FinishedMatchesPersistenceService persistenceService =
                new FinishedMatchesPersistenceServiceImpl(matchDao, playerService);
        OngoingMatchesService ongoingMatchesService = new OngoingMatchesServiceImpl(persistenceService);
        this.matchService = new MatchServiceImpl(ongoingMatchesService);
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