package com.example.tennisscoreboard.util;

import com.example.tennisscoreboard.exception.ValidationException;

import java.util.UUID;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯЁё\\s\\-']+$");
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final long MIN_PAGE = 1L;
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;

    private Validator() {
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name should not be empty.");
        }
        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new ValidationException("Name should not start or end with a space.");
        }
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters long.");
        }
        if (!ALLOWED_CHARS_PATTERN.matcher(name).matches()) {
            throw new ValidationException("Name can contain only Russian or English letters, spaces, hyphens, and apostrophes.");
        }
    }

    public static void validateNames(String playerOneName, String playerTwoName) {
        validateName(playerOneName);
        validateName(playerTwoName);
        if (playerOneName.equals(playerTwoName)) {
            throw new ValidationException("Player names must be different");
        }
    }

    public static void validateUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new ValidationException("UUID should not be empty.");
        }
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format.");
        }
    }

    public static UUID parseUuid(String uuid) {
        validateUuid(uuid);
        return UUID.fromString(uuid);
    }

    public static void validatePage(String pageStr) {
        if (pageStr == null || pageStr.isBlank()) {
            return;
        }
        try {
            long page = Long.parseLong(pageStr.trim());
            if (page < MIN_PAGE) {
                throw new ValidationException("Page starts with 1.");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid page format.");
        }
    }

    public static long parsePage(String pageStr) {
        validatePage(pageStr);
        if (pageStr == null || pageStr.isBlank()) {
            return MIN_PAGE;
        }
        return Long.parseLong(pageStr.trim());
    }

    public static void validatePlayerNumber(String playerNumber) {
        if (playerNumber == null || playerNumber.isBlank()) {
            throw new ValidationException("Player number is required");
        }
        try {
            int number = Integer.parseInt(playerNumber);
            if (number != PLAYER_ONE && number != PLAYER_TWO) {
                throw new ValidationException("Player number must be 1 or 2");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid player number format");
        }
    }

    public static int parsePlayerNumber(String playerNumber) {
        validatePlayerNumber(playerNumber);
        return Integer.parseInt(playerNumber);
    }
}