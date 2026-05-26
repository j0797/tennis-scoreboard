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

    private static final long DEFAULT_PAGE_NUMBER = 1L;
    private static final Logger log = LoggerFactory.getLogger(FinishedMatchesController.class);
    private static final String PARAM_FILTER = "filter_by_player_name";
    private static final String PARAM_PAGE = "page";
    private static final String ATTR_MATCHES = "matches";
    private static final String ATTR_TOTAL_PAGES = "totalPages";
    private static final String ATTR_CURRENT_PAGE = "currentPage";
    private static final String VIEW_MATCHES = "/WEB-INF/jsp/matches.jsp";
    private final FinishedMatchesPersistenceServiceImpl service = new FinishedMatchesPersistenceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter(PARAM_FILTER);
        String pageNumberParam = request.getParameter(PARAM_PAGE);
        log.info("GET /matches with filter='{}', page='{}'", playerName, pageNumberParam);
        long page = DEFAULT_PAGE_NUMBER;
        if (pageNumberParam != null) {

            // Validator лучше внедрять через метод init(), а не обращать к нему напрямую из этого метода
            page = Validator.validatePage(pageNumberParam);
        }
        PaginationResponseDto<MatchDto> result = service.getFinishedMatches(playerName, page);

        // Данные о странице передаются по частям (хотя для этого есть специальный PaginationResponseDto). Лучше передавать сам DTO (и добавить в него currentPage)
        request.setAttribute(ATTR_MATCHES, result.items());
        request.setAttribute(ATTR_TOTAL_PAGES, result.totalPages());
        request.setAttribute(ATTR_CURRENT_PAGE, page);
        log.debug("Found {} matches, totalPages={}", result.items().size(), result.totalPages());
        request.getRequestDispatcher(VIEW_MATCHES).forward(request, response);
    }
}