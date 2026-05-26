package com.example.tennisscoreboard.dto;

public record ScoreDto(String pointsPlayer1, String pointsPlayer2, String games, String sets,
                       boolean tieBreak, String tieBreakPoints) {

}