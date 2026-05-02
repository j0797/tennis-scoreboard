package com.example.tennisscoreboard.dto;

import lombok.Value;

@Value
public class MatchDto {

    // для DTO идеально подходит record

    Long id;
    PlayerDto player1;
    PlayerDto player2;
    PlayerDto winner;
}