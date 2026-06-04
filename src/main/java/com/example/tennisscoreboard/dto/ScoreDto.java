package com.example.tennisscoreboard.dto;

public record ScoreDto(String firstPlayerName,
                       String secondPlayerName,
                       String winnerName,
                       String pointsFirstPlayer,
                       String pointsSecondPlayer,
                       String games,
                       String sets,
                       boolean tieBreak,
                       String tieBreakPoints) {

}