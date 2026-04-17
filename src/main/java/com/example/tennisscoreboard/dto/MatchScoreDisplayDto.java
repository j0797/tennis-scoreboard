package com.example.tennisscoreboard.dto;

import lombok.Value;

@Value
public class MatchScoreDisplayDto {
    String pointsPlayer1;
    String pointsPlayer2;
    String games;
    String sets;
    boolean tieBreak;
    String tieBreakPoints;
}