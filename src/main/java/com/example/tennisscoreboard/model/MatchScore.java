package com.example.tennisscoreboard.model;

import lombok.Getter;

@Getter
public class MatchScore {

    private int playerOnePoints = 0;
    private int playerTwoPoints = 0;

    public void addPointToPlayer(int playerNumber) {
        if (playerNumber == 1) playerOnePoints++;
        else playerTwoPoints++;
    }

}