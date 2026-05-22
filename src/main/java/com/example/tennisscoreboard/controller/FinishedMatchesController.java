package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.service.impl.FinishedMatchesPersistenceServiceImpl;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/matches")
public class FinishedMatchesController extends HttpServlet {

    // TODO: Зависимость `FinishedMatchesPersistenceService` создаётся напрямую в месте объявления. Вместо этого стоит внедрять зависимости через `init()` метод сервлета.

    // Все повторяющиеся или важные строковые литералы лучше вынести в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    private final FinishedMatchesPersistenceServiceImpl service = new FinishedMatchesPersistenceServiceImpl();

    // Константы объявляются первыми (пишутся в самом верху) в классе. long DEFAULT_PAGE_NUMBER, а также Logger log должны быть выше FinishedMatchesPersistenceService service
    private static final long DEFAULT_PAGE_NUMBER = 1L;
    private static final Logger log = LoggerFactory.getLogger(FinishedMatchesController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("filter_by_player_name");
        String pageNumberParam = request.getParameter("page");
        log.info("GET /matches with filter='{}', page='{}'", playerName, pageNumberParam);
        long page = DEFAULT_PAGE_NUMBER;
        if (pageNumberParam != null) {

            // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
            page = Validator.validatePage(pageNumberParam);
        }
        PaginationResponseDto<MatchDto> result = service.getFinishedMatches(playerName, page);

        // Данные о странице передаются по частям (хотя для этого есть специальный PaginationResponseDto). Лучше передавать сам DTO (и добавить в него currentPage)
        request.setAttribute("matches", result.getItems());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("currentPage", page);
        log.debug("Found {} matches, totalPages={}", result.getItems().size(), result.getTotalPages());
        request.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(request, response);
    }
}