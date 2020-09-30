package org.subhashis.cricketscoreboardupdate.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.subhashis.cricketscoreboardupdate.domain.Player;
import reactor.core.publisher.Flux;

public interface PlayerReactiveRepository extends ReactiveMongoRepository<Player,String>, PlayerReactiveRepositoryCustom {
}
