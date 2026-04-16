package com.example.tennisscoreboard.dto;

import lombok.Value;

@Value
public class MatchDto {
    Long id;
    PlayerDto player1;
    PlayerDto player2;
    PlayerDto winner;
}