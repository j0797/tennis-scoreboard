package com.example.tennisscoreboard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScore {

    private int playerOnePoints = 0;
    private int playerTwoPoints = 0;
    private int playerOneGames = 0;
    private int playerTwoGames = 0;
    private int playerOneSets = 0;
    private int playerTwoSets = 0;
    private int playerOneTieBreakPoints = 0;
    private int playerTwoTieBreakPoints = 0;
    private boolean playerOneAdvantage = false;
    private boolean playerTwoAdvantage = false;

}