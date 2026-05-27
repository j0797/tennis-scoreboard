package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.mapper.MatchScoreDisplayMapper;
import com.example.tennisscoreboard.model.TennisMatch;
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

    // TODO: Сервлет берёт на себя лишнюю ответственность — оркестрирует взаимодействие между несколькими сервисами,
    // хотя его задача — только принимать HTTP-запросы и делегировать их обработку. Это нарушает принцип единственной ответственности (SRP)
    // и делает код сервлета более сложным и трудным для тестирования.
    // Сервлет должен быть "тонким контроллером", делегирующим всю бизнес-логику одному фасадному сервису.
    // (см. файл "Архитектурный анти-паттерн: "Толстый контроллер" (Fat Controller).md" в этом же пакете)

    private static final Logger log = LoggerFactory.getLogger(MatchScoreController.class);
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_PLAYER = "player";
    private static final String ATTR_DISPLAY_DTO = "displayDto";
    private static final String ATTR_MATCH_OVER = "matchOver";
    private static final String VIEW_MATCH_SCORE = "/WEB-INF/jsp/match-score.jsp";
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String matchIdParam = request.getParameter(PARAM_UUID);
        log.debug("GET match-score with uuid={}", matchIdParam);

        // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        UUID id = Validator.validateUuid(matchIdParam);
        TennisMatch tennisMatch = ongoingMatchesService.getOngoingMatch(id);
        if (tennisMatch == null) {
            throw new NotFoundException("Match not found");
        }

        ScoreDto displayDto = MatchScoreDisplayMapper.toDisplayDto(tennisMatch);
        request.setAttribute(ATTR_DISPLAY_DTO, displayDto);
        request.getRequestDispatcher(VIEW_MATCH_SCORE).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String matchIdParam = request.getParameter(PARAM_UUID);
        String playerNumberParam = request.getParameter(PARAM_PLAYER);
        log.info("POST match-score: match={}, player={}", matchIdParam, playerNumberParam);

        // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        UUID id = Validator.validateUuid(matchIdParam);

        // Название player должно быть только у объектов типа Player, а остальным переменным стоит подбирать более подходящие названия, сответствующие их типу и смыслу.
        int player = Validator.validatePlayerNumber(playerNumberParam);

        // Здесь переменная OngoingMatch называется match, а в методе doGet называется ongoingMatch. Лучше придерживаться одного подхода в именовании.
        TennisMatch match = ongoingMatchesService.getOngoingMatch(id);
        if (match == null) {
            log.warn("Match not found for uuid {}", id);

            // Нигде нет обработки этого исключения, значит пользователь увидит страницу с ошибкой 500 (Internal Server Error), хотя не найденный матч скорее соответствует 400 (Bad Request).
            throw new NotFoundException("Match not found");
        }

        ongoingMatchesService.addPoint(id, player);

        if (match.isOver()) {
            log.info("Match {} finished after point by player {}", id, player);
            ScoreDto displayDto = MatchScoreDisplayMapper.toDisplayDto(match);
            request.setAttribute(ATTR_DISPLAY_DTO, displayDto);
            request.setAttribute(ATTR_MATCH_OVER, true);
            request.getRequestDispatcher(VIEW_MATCH_SCORE).forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + id);
        }
    }
}