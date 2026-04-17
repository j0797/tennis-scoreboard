package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
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
        String playerName = request.getParameter("playerName");
        String pageNumber = request.getParameter("page");
        long page = DEFAULT_PAGE_NUMBER;

        if (pageNumber != null) {
            try {
                page = Long.parseLong(pageNumber);
                if (page < 1) {
                    page = DEFAULT_PAGE_NUMBER;
                }
            } catch (NumberFormatException e) {
                page = DEFAULT_PAGE_NUMBER;
            }
        }
        PaginationResponseDto<MatchDto> result = service.getFinishedMatches(playerName, page);
        request.setAttribute("matches", result.getItems());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("currentPage", page);
        request.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(request, response);
    }
}