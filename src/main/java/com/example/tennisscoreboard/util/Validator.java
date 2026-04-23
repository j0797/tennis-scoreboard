package com.example.tennisscoreboard.util;

import com.example.tennisscoreboard.exception.ValidationException;

import java.util.UUID;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯЁё\\s\\-']+$");

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name should not be empty.");
        }
        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new ValidationException("Name should not start or end with a space.");
        }
        if (name.length() < 2 || name.length() > 30) {
            throw new ValidationException("Name must be between 2 and 30 characters long.");
        }
        if (!ALLOWED_CHARS_PATTERN.matcher(name).matches()) {
            throw new ValidationException("Name can contain only Russian or English letters, spaces, hyphens, and apostrophes.");
        }
    }

    public static UUID validateUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new ValidationException("UUID should not be empty.");
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format.");
        }
    }

    public static long validatePage(String pageStr) {
        if (pageStr == null || pageStr.isBlank()) {
            return 1L;
        }
        try {
            long page = Long.parseLong(pageStr.trim());
            if (page < 1) {
                throw new ValidationException("Page starts with 1.");
            }
            return page;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid page format.");
        }
    }

    public static int validatePlayerNumber(String playerNumber) {
        if (playerNumber == null || playerNumber.isBlank()) {
            throw new ValidationException("Player number is required");
        }
        try {
            int number = Integer.parseInt(playerNumber);
            if (number != 1 && number != 2) {
                throw new ValidationException("Player number must be 1 or 2");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid player number format");
        }
    }
}