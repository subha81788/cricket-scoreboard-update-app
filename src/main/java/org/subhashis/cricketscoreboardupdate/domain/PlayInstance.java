package org.subhashis.cricketscoreboardupdate.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayInstance {
    private Team battingTeam;
    private Team fieldingTeam;
    private Batsman striker;
    private Batsman nonStriker;
    private Bowler bowler;
    private WicketKeeper wicketKeeper;
    private Fielder fielder;
    private List<Fielder> fielders;
    private int totalScore;
    private int over;
    private int ball;
    private int wicketsTaken;
    private int nextBatsmanIndex;
}

