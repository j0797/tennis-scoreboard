package com.example.tennisscoreboard.dto;

public record MatchDto(Long id, PlayerDto player1, PlayerDto player2, PlayerDto winner) {

}