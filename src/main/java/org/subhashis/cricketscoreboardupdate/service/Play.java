package org.subhashis.cricketscoreboardupdate.service;

import org.springframework.stereotype.Service;
import org.subhashis.cricketscoreboardupdate.domain.PlayEvent;
import org.subhashis.cricketscoreboardupdate.domain.ScoreBoard;
import org.subhashis.cricketscoreboardupdate.domain.Team;
import org.subhashis.cricketscoreboardupdate.enums.ScoreType;
import org.subhashis.cricketscoreboardupdate.enums.WicketType;
import reactor.core.publisher.Mono;

@Service
public interface Play {
    public void startGame();
    public void startPlay(Team battingTeam, Team fieldingTeam);
    public Mono<PlayEvent> blowBat();
    public Mono<ScoreBoard> adjustPlayInstance(PlayEvent playEvent);
    public WicketType onBatsmanGotOut();
    public void adjustOver(ScoreType scoreType);
    public ScoreBoard updateScoreBoard();
}
