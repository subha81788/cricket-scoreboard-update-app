package org.subhashis.cricketscoreboardupdate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreBoard {
    private int score;
    private int over;
    private int ball;
    private int wickets;
}
