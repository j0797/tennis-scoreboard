package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.config.ApplicationContext;
import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
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

@WebServlet("/matches")
public class FinishedMatchesController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(FinishedMatchesController.class);
    private static final String PARAM_FILTER = "filter_by_player_name";
    private static final String PARAM_PAGE = "page";
    private static final String VIEW_MATCHES = "/WEB-INF/jsp/matches.jsp";
    private static final int DEFAULT_PAGE_SIZE = 5;
    private FinishedMatchesPersistenceService persistenceService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("appContext");
        this.persistenceService = context.getPersistenceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter(PARAM_FILTER);
        String pageNumberParam = request.getParameter(PARAM_PAGE);
        log.info("GET /matches with filter='{}', page='{}'", playerName, pageNumberParam);
        long page = Validator.parsePage(pageNumberParam);
        PaginationResponseDto<MatchDto> result = persistenceService.getFinishedMatches(playerName, page, DEFAULT_PAGE_SIZE);
        request.setAttribute("pagination", result);
        log.debug("Found {} matches, totalPages={}", result.items().size(), result.totalPages());
        request.getRequestDispatcher(VIEW_MATCHES).forward(request, response);
    }
}