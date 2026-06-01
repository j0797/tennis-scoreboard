package com.example.tennisscoreboard.filter;

import com.example.tennisscoreboard.exception.DatabaseException;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.exception.ValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingFilter.class);
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ValidationException e) {
            log.warn("Validation error: {}", e.getMessage());
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Not found: {}", e.getMessage());
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            log.error("Database error", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred. Please try again later.");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().write(message);
    }
}