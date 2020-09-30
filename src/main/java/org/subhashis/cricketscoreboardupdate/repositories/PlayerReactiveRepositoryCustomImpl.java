package org.subhashis.cricketscoreboardupdate.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.subhashis.cricketscoreboardupdate.domain.Player;
import reactor.core.publisher.Flux;

public class PlayerReactiveRepositoryCustomImpl implements PlayerReactiveRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public PlayerReactiveRepositoryCustomImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Player> findAllByTeamId(String teamId) {
        Query query = new Query(Criteria.where("teamId").is(teamId));
        return mongoTemplate.find(query,Player.class);
    }
}
