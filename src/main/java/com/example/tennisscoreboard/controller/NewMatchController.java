package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.exception.ValidationException;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.service.PlayerService;
import com.example.tennisscoreboard.service.impl.FinishedMatchesPersistenceServiceImpl;
import com.example.tennisscoreboard.service.impl.OngoingMatchesServiceImpl;
import com.example.tennisscoreboard.service.impl.PlayerServiceImpl;
import com.example.tennisscoreboard.util.HibernateUtil;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchController extends HttpServlet {

    // TODO: Сервлет отправляет сообщение из исключения (`e.getMessage()`) напрямую пользователю для `ValidateException`.
    // Сообщения об ошибках из исключений могут содержать технические детали, которые не предназначены
    // для конечного пользователя и могут представлять угрозу безопасности. Например, сообщение может быть
    // `"No entity found for query 'SELECT ...'"` или `"Validation failed for field 'internalFieldName'"`,
    // что раскрывает структуру БД или внутренние имена полей.
    //
    // Лучше никогда не отправлять необработанное сообщение из исключения на клиент.
    // Вместо этого можно использовать заранее определённые, безопасные сообщения или коды ошибок.
    // Само исключение при этом нужно логировать для разработчиков.
    //
    // Это повысит безопасность приложения и улучшит пользовательский опыт при возникновении ошибок.

    private static final Logger log = LoggerFactory.getLogger(NewMatchController.class);
    private static final String PARAM_PLAYER_ONE = "playerOneName";
    private static final String PARAM_PLAYER_TWO = "playerTwoName";
    private static final String ATTR_ERROR = "error";
    private static final String VIEW_NEW_MATCH = "/WEB-INF/jsp/new-match.jsp";
    private OngoingMatchesService ongoingMatchesService;

    @Override
    public void init() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PlayerDao playerDao = new PlayerDao(sessionFactory);
        MatchDao matchDao = new MatchDao(sessionFactory);
        PlayerService playerService = new PlayerServiceImpl(playerDao);
        FinishedMatchesPersistenceService persistenceService =
                new FinishedMatchesPersistenceServiceImpl(matchDao, playerService);
        this.ongoingMatchesService = new OngoingMatchesServiceImpl(persistenceService);
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

            // Сервлет не должен заниматься оркестрацией валидации. Лучше создавать один DTO c именами игроков и передавать в валидатор его.
            Validator.validateName(playerOneName);
            Validator.validateName(playerTwoName);

            // Сервлет не должен заниматься валидацией — эта логика должна быть реализована в самом валидаторе.
            if (playerOneName.equals(playerTwoName)) {
                throw new ValidationException("Player names must be different");
            }

            UUID matchId = ongoingMatchesService.createMatch(playerOneName, playerTwoName);
            log.info("Match created with id {}", matchId);
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + matchId);

            // Логику обработки исключений можно реализовать в фильтре. Так она будет централизована для всего приложения и её части не будут повторяться в разных местах.
        } catch (ValidationException e) {
            log.warn("Validation error while creating match: {}", e.getMessage());

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            request.setAttribute(ATTR_ERROR, e.getMessage());
            request.getRequestDispatcher(VIEW_NEW_MATCH).forward(request, response);
        }
    }
}