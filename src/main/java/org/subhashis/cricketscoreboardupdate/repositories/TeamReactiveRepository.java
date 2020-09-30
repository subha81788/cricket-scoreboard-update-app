package org.subhashis.cricketscoreboardupdate.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.subhashis.cricketscoreboardupdate.domain.Team;

@Repository
public interface TeamReactiveRepository extends ReactiveMongoRepository<Team,String> {
}
