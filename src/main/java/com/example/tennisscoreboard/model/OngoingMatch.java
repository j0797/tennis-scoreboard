package com.example.tennisscoreboard.model;

import com.example.tennisscoreboard.entity.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OngoingMatch {
    private Player playerOne;
    private Player playerTwo;
    private Player winner;
    private MatchScore score;
    private boolean matchOver;
    private boolean openTieBreak = false;
    private boolean deuceSituation = false;

    public OngoingMatch(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.score = new MatchScore();
        this.matchOver = false;
        this.openTieBreak = false;
        this.deuceSituation = false;
    }
}
