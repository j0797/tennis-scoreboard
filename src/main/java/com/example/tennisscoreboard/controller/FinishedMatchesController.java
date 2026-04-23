package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/matches")
public class FinishedMatchesController extends HttpServlet {
    private final FinishedMatchesPersistenceService service = new FinishedMatchesPersistenceService();
    private static final long DEFAULT_PAGE_NUMBER = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("filter_by_player_name");
        String pageNumberParam = request.getParameter("page");
        long page = DEFAULT_PAGE_NUMBER;
        if (pageNumberParam != null) {
            page = Validator.validatePage(pageNumberParam);
        }
        PaginationResponseDto<MatchDto> result = service.getFinishedMatches(playerName, page);
        request.setAttribute("matches", result.getItems());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("currentPage", page);
        request.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(request, response);
    }
}