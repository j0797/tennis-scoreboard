package com.example.tennisscoreboard.controller;

import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.entity.Player;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;


@WebServlet("/new-match")
public class NewMatchController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp").forward(request, response);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String playerOneName = request.getParameter("playerOneName");
        String playerTwoName = request.getParameter("playerTwoName");

        Player playerOne = new Player(playerOneName);
        Player playerTwo = new Player(playerTwoName);
        Match match = new Match(playerOne, playerTwo, null);

        request.setAttribute("playerOne", playerOne);
        request.setAttribute("playerTwo", playerTwo);
        request.setAttribute("match", match);

        response.sendRedirect(request.getContextPath() + "/match-score?id=" + match.getId());
    }
}