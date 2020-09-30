package org.subhashis.cricketscoreboardupdate.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.subhashis.cricketscoreboardupdate.domain.Contribution;
import org.subhashis.cricketscoreboardupdate.domain.Player;
import org.subhashis.cricketscoreboardupdate.domain.ScoreBoard;
import org.subhashis.cricketscoreboardupdate.domain.Team;
import org.subhashis.cricketscoreboardupdate.repositories.PlayerReactiveRepository;
import org.subhashis.cricketscoreboardupdate.repositories.TeamReactiveRepository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.subhashis.cricketscoreboardupdate.enums.Role.*;

@Slf4j
@Profile("dev")
@Component
public class DataInitializerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private TeamReactiveRepository teamReactiveRepository;

    @Autowired
    private PlayerReactiveRepository playerReactiveRepository;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    private List<Team> playingTeams;
    private Map<String,List<Player>> playersMap;

    public DataInitializerBootstrap() {
    }

    public DataInitializerBootstrap(TeamReactiveRepository teamReactiveRepository, PlayerReactiveRepository playerReactiveRepository) {
        this.teamReactiveRepository = teamReactiveRepository;
        this.playerReactiveRepository = playerReactiveRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        createCappedCollection();
        loadTeamsData();
    }

    private void createCappedCollection() {
        reactiveMongoTemplate.dropCollection(ScoreBoard.class);
        reactiveMongoTemplate.createCollection(ScoreBoard.class, CollectionOptions.empty().capped().maxDocuments(5000).size(5242880));
    }

    private void loadTeamsData() {
        playingTeams = new ArrayList<>();
        playersMap = new HashMap<>();

        log.info("LOADING TEAMS DATA....");

        var teamIndia = new Team("IND", "INDIA MEN INTERNATION CRICKET TEAM");
        var teamAussie = new Team("AUS", "AUSTRALIAN MEN INTERNATION CRICKET TEAM");
        this.playingTeams.add(teamIndia);
        this.playingTeams.add(teamAussie);
        log.info("playing teams " + this.playingTeams);

        teamReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(List.of(teamIndia, teamAussie)))
                .flatMap(teamReactiveRepository::save)
                .subscribe(t -> log.info("Team saved " + t));

        this.playersMap = loadTeamPlayers();

        playerReactiveRepository.deleteAll();

        this.playersMap.forEach((teamId,playerList) -> {
            Flux.fromIterable(playerList)
                    .flatMap(playerReactiveRepository::save)
                    .subscribe(p -> log.info("Player saved " + p));
        });

    }

    private Map<String,List<Player>> loadTeamPlayers() {
        Map<String,List<Player>> playersMap = new HashMap<>();
        var initContribution = new Contribution(0,0,0,new ArrayList<>(),0,new ArrayList<>(),0,new ArrayList<>());
        var dhawan = new Player("001", "IND", "Shikhar Dhawan", List.of(BATSMAN),initContribution);
        var rohit = new Player("002", "IND", "Rohit Sharma", List.of(BATSMAN),initContribution);
        var kohli = new Player("003", "IND", "Virat Kohli", List.of(CAPTAIN, BATSMAN), initContribution );
        var rahul = new Player("004", "IND", "K.L. Rahul", List.of(BATSMAN),initContribution);
        var dhoni = new Player("005", "IND", "Mahendra Singh Dhoni", List.of(WICKETKEEPER, BATSMAN),initContribution);
        var jadeja = new Player("006", "IND", "Ravindra Jadeja", List.of(BOWLER),initContribution);
        var hardik = new Player("007", "IND", "Hardik Pandey", List.of(ALL_ROUNDER),initContribution);
        var bhuvneshwar = new Player("008", "IND", "Bhuvneshwar Kumar", List.of(BOWLER),initContribution);
        var chahal = new Player("009", "IND", "Yuzvendra Chahal", List.of(BOWLER),initContribution);
        var kuldeep = new Player("010", "IND", "Kuldeep Yadav", List.of(BOWLER),initContribution);
        var bumrah = new Player("011", "IND", "Jaspreet Bumrah", List.of(BOWLER),initContribution);
        List<Player> teamIndiaPlayers = List.of(dhawan, rohit, kohli, rahul, dhoni, jadeja, hardik, bhuvneshwar, chahal, chahal, kuldeep, bumrah);
        playersMap.put("IND",teamIndiaPlayers);

        var finch = new Player("101", "AUS", "Aaron Finch", List.of(CAPTAIN, BATSMAN), initContribution);
        var warner = new Player("102", "AUS", "David Warner", List.of(BATSMAN), initContribution);
        var smith = new Player("103", "AUS", "Steven Smith", List.of(BATSMAN), initContribution);
        var maxwell = new Player("104", "AUS", "Glenn Maxwell", List.of(BATSMAN), initContribution);
        var carey = new Player("105", "AUS", "Alex Carey", List.of(WICKETKEEPER,BATSMAN), initContribution);
        var marsh = new Player("106", "AUS", "Mitchell Marsh", List.of(ALL_ROUNDER), initContribution);
        var stoinis = new Player("107", "AUS", "Marcus Stoinis", List.of(BATSMAN), initContribution);
        var cummins = new Player("108", "AUS", "Pat Cummins", List.of(VICE_CAPTAIN, BOWLER), initContribution);
        var lyon = new Player("109", "AUS", "Nathan Lyon", List.of(BOWLER), initContribution);
        var starc = new Player("110", "AUS", "Mitchell Starc", List.of(BOWLER), initContribution);
        var zampa = new Player("111", "AUS", "Adam Zampa", List.of(BOWLER), initContribution);
        List<Player> teamAustraliaPlayers = List.of(finch, warner, smith, maxwell, carey, marsh, stoinis, cummins, lyon, starc, zampa);
        playersMap.put("AUS",teamAustraliaPlayers);

        return playersMap;
    }
}
