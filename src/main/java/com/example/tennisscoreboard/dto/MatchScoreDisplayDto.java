package com.example.tennisscoreboard.dto;

import lombok.Value;

@Value
public class MatchScoreDisplayDto {

    // можно назвать просто MatchScoreDto или ScoreDto

    // для DTO идеально подходит record

    // счёт в гейме хранится в отдельных полях для каждого игрока, а счёт в сете, матче и тай-брейке — в одном String через двоеточие.
        // Хранить счёт каждого игрока в отдельном поле выглядит более логичным и естественным.

    String pointsPlayer1;
    String pointsPlayer2;
    String games;
    String sets;
    boolean tieBreak;
    String tieBreakPoints;
}