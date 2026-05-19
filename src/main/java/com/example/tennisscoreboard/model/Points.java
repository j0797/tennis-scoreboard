package com.example.tennisscoreboard.model;

public enum Points {
    LOVE,
    FIFTEEN,
    THIRTY,
    FORTY,
    ADVANTAGE,
    WON;

    public Points next() {
        return switch (this) {
            case LOVE -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            default -> throw new IllegalStateException("No simple next() from " + this);
        };
    }
}