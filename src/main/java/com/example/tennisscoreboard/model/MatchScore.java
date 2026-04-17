package com.example.tennisscoreboard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScore {
    private int playerOnePoints;
    private int playerTwoPoints;
    private int playerOneGames;
    private int playerTwoGames;
    private int playerOneSets;
    private int playerTwoSets;
    private int playerOneTieBreakPoints;
    private int playerTwoTieBreakPoints;
    private boolean playerOneAdvantage;
    private boolean playerTwoAdvantage;
}