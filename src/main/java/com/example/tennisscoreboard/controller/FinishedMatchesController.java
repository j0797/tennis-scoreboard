package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.dto.PaginationResponseDTO;
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
        long page = pageNumber != null ? Long.parseLong(pageNumber) : DEFAULT_PAGE_NUMBER;

        PaginationResponseDTO result = service.getFinishedMatches(playerName, page);
        request.setAttribute("matches", result.getMatches());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("currentPage", page);
        request.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(request, response);
    }
}