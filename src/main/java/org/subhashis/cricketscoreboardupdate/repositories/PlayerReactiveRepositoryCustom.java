package org.subhashis.cricketscoreboardupdate.repositories;

import org.subhashis.cricketscoreboardupdate.domain.Player;
import reactor.core.publisher.Flux;

public interface PlayerReactiveRepositoryCustom {
    public Flux<Player> findAllByTeamId(String teamId);
}
