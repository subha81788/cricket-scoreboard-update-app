package org.subhashis.cricketscoreboardupdate.configs;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(basePackages = "org.subhashis.cricketscoreboardupdate.repositories")
@Configuration
@Profile("dev")
public class DataSourceReactiveMongoConfigDev extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String MONGO_DB_URI;

    @Value("${spring.data.mongodb.db}")
    private String MONGO_DB_DBNAME;

    @Override
    protected String getDatabaseName() {
        return MONGO_DB_DBNAME;
    }

    @Bean
    public MongoClient mongoClient() {
        /*
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGO_DB_URI))
                .build();
         */

        return MongoClients.create();
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), getDatabaseName());
    }
}
