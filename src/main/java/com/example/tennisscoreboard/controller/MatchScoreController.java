package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchScoreDisplayDto;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.mapper.MatchScoreDisplayMapper;
import com.example.tennisscoreboard.model.OngoingMatch;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.util.Validator;
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

    // TODO: Зависимости используются через прямое обращение к сервисам из методов. Вместо этого стоит внедрять зависимости через `init()` метод сервлета.

    // Все повторяющиеся или важные строковые литералы лучше вынести в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: Контроллер передаёт в слой представления доменные модели.
        // Передача доменных моделей в JSP не является хорошей практикой. Это нарушает принцип разделения ответственности между слоями
        // и связывает слой представления с моделью данных (что чревато ошибками, например, в случае переименования полей).
        // Лучше использовать DTO (Data Transfer Object) для передачи данных в представление.
        // DTO позволяют контролировать, какие именно данные передаются.

    // TODO: Сервлет берёт на себя лишнюю ответственность — оркестрирует взаимодействие между несколькими сервисами,
        // хотя его задача — только принимать HTTP-запросы и делегировать их обработку. Это нарушает принцип единственной ответственности (SRP)
        // и делает код сервлета более сложным и трудным для тестирования.
        // Сервлет должен быть "тонким контроллером", делегирующим всю бизнес-логику одному фасадному сервису.
        // (см. файл "Архитектурный анти-паттерн: "Толстый контроллер" (Fat Controller).md" в этом же пакете)

    private static final Logger log = LoggerFactory.getLogger(MatchScoreController.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String matchIdParam = request.getParameter("uuid");
        log.debug("GET match-score with uuid={}", matchIdParam);

        // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        UUID id = Validator.validateUuid(matchIdParam);

        // OngoingMatchesService лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        // Сервлет не должен работать с доменными моделями
        OngoingMatch ongoingMatch = OngoingMatchesService.getOngoingMatch(id);
        if (ongoingMatch == null) {
            throw new NotFoundException("Match not found");
        }

        // MatchScoreDisplayMapper лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        MatchScoreDisplayDto displayDto = MatchScoreDisplayMapper.toDisplayDto(ongoingMatch);
        request.setAttribute("displayDto", displayDto);

        // Сервлет не должен передавать доменные модели во View
        request.setAttribute("match", ongoingMatch);
        request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String matchIdParam = request.getParameter("uuid");
        String playerNumberParam = request.getParameter("player");
        log.info("POST match-score: match={}, player={}", matchIdParam, playerNumberParam);

        // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        UUID id = Validator.validateUuid(matchIdParam);

        // Название player должно быть только у объектов типа Player, а остальным переменным стоит подбирать более подходящие названия, сответствующие их типу и смыслу.
        int player = Validator.validatePlayerNumber(playerNumberParam);

        // OngoingMatchesService лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        // Сервлет не должен работать с доменными моделями
        // Здесь переменная OngoingMatch называется match, а в методе doGet называется ongoingMatch. Лучше придерживаться одного подхода в именовании.
        OngoingMatch match = OngoingMatchesService.getOngoingMatch(id);
        if (match == null) {
            log.warn("Match not found for uuid {}", id);

            // Нигде нет обработки этого исключения, значит пользователь увидит страницу с ошибкой 500 (Internal Server Error), хотя не найденный матч скорее соответствует 400 (Bad Request).
            throw new NotFoundException("Match not found");
        }

        // OngoingMatchesService лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
        OngoingMatchesService.addPoint(id, player);

        if (match.isMatchOver()) {
            log.info("Match {} finished after point by player {}", id, player);

            // MatchScoreDisplayMapper лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
            MatchScoreDisplayDto displayDto = MatchScoreDisplayMapper.toDisplayDto(match);
            request.setAttribute("displayDto", displayDto);

            // Сервлет не должен передавать доменные модели во View
            request.setAttribute("match", match);
            request.setAttribute("matchOver", true);
            request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + id);
        }
    }
}