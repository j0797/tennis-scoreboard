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

    // TODO: Зависимость `PlayerService` создаётся напрямую в месте объявления. Вместо этого стоит внедрять зависимости через `init()` метод сервлета.

    // Все повторяющиеся или важные строковые литералы лучше вынести в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: После валидации имён игроков, сервлет получает JPA Entity игроков (`Player`) из `PlayerService` только для того, чтобы передать их в `OngoingMatchesService.createMatch(p1, p2)`.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете).
        // Сервлет не должен работать с JPA сущностями и знать о существовании класса `Player` — ему это не нужно для выполнения его задачи.
        // Он должен общаться с сервисным слоем исключительно через объекты передачи данных (DTO).
        //
        // Сервисный слой должен возвращать только те данные, которые необходимы контроллеру.
        // В данном случае, сервлету нужен только ID созданного матча для редиректа.
        // Идеальная картина для него — использовать только один сервис (например, `OngoingMatchesService`) —
        // отправлять ему входящие данные и получать ответ, который нужно отдать в представление.
        // А логикой создания матча пусть управляет сервисный слой. Такой рефакторинг сделает контроллер "тонким"
        // и его единственной задачей останется обработка HTTP и делегирование бизнес-запроса сервисному слою.

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

            // Сервлет не должен заниматься оркестрацией валидации. Лучше создавать один DTO c именами игроков и передавать в валидатор его.
            // Валидатор тоже лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
            Validator.validateName(playerOneName);
            Validator.validateName(playerTwoName);

            // Сервлет не должен заниматься валидацией — эта логика должна быть реализована в самом валидаторе.
            if (playerOneName.equals(playerTwoName)) {
                throw new ValidationException("Player names must be different");
            }

            // TODO: Сервлет не должен работать с Entity — эта логика должна быть в сервисе
            // Лучше давать переменным полные имена.
            Player p1 = playerService.findOrCreatePlayer(playerOneName);
            Player p2 = playerService.findOrCreatePlayer(playerTwoName);
            UUID matchId = OngoingMatchesService.createMatch(p1, p2);
            log.info("Match created with id {}", matchId);
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + matchId);

        // Логику обработки исключений можно реализовать в фильтре. Так она будет централизована для всего приложения и её части не будут повторяться в разных местах.
        } catch (ValidationException e) {
            log.warn("Validation error while creating match: {}", e.getMessage());

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp").forward(request, response);
        }
    }
}