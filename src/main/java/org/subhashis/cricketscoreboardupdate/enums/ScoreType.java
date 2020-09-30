package org.subhashis.cricketscoreboardupdate.enums;

public enum ScoreType {

    ZERO(0), LEGBY_ONE(1), WIDE_BALL(1), NO_BALL(1), LEGBY_TWO(2), LEGBY_THREE(3), RUN_ONE(1), RUN_TWO(2), RUN_THREE(3), FOUR(4), SIX(6);

    private final int score;

    ScoreType(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }
}
