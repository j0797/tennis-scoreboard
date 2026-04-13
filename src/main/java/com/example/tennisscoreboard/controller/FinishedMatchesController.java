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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter("playerName");
        String pageParam = req.getParameter("page");
        long page = pageParam != null ? Long.parseLong(pageParam) : 1L;

        PaginationResponseDTO result = service.getFinishedMatches(playerName, page);
        req.setAttribute("matches", result.getMatches());
        req.setAttribute("totalPages", result.getTotalPages());
        req.setAttribute("currentPage", page);
        req.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(req, resp);
    }
}