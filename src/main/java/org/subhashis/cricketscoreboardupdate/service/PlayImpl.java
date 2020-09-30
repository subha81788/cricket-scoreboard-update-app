package org.subhashis.cricketscoreboardupdate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subhashis.cricketscoreboardupdate.enums.ScoreType;
import org.subhashis.cricketscoreboardupdate.enums.WicketType;
import org.subhashis.cricketscoreboardupdate.domain.*;
import org.subhashis.cricketscoreboardupdate.repositories.PlayerReactiveRepository;
import org.subhashis.cricketscoreboardupdate.repositories.TeamReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.subhashis.cricketscoreboardupdate.enums.Role.*;
import static org.subhashis.cricketscoreboardupdate.constants.Constants.*;

@Slf4j
@Service
public class PlayImpl implements Play {
    private Random random;
    private ScoreType prevScore;

    @Autowired
    private TeamReactiveRepository teamReactiveRepository;

    @Autowired
    private PlayerReactiveRepository playerReactiveRepository;

    private PlayInstance playInstance;

    public PlayImpl() {
        this.random = new Random();
        this.playInstance = new PlayInstance();
    }

    @Override
    public void startGame() {

        var battingTeam = getBattingTeamByToss();
        var fieldingTeam = getFieldingTeam();

        startPlay(battingTeam,fieldingTeam);

        startPlay(fieldingTeam, battingTeam);

    }

    @Override
    public void startPlay(Team battingTeam, Team fieldingTeam) {

        playInit(battingTeam, fieldingTeam);

        var playEventFlux = Flux.interval(Duration.ofSeconds(INTERVAL_IN_SEC))
                .flatMap(i -> this.blowBat())
                .flatMap(e -> adjustPlayInstance(e))
                .doOnNext(s -> log.info(s.toString()))
                .takeWhile(s -> s.getOver() <= OVER_LIMIT)
                .subscribe(i -> log.info("One more ball played"));
    }

    @Override
    public Mono<PlayEvent> blowBat() {
        int pick = random.nextInt(ScoreType.values().length);
        var score = List.of(ScoreType.values()).get(pick);

        pickFielderInAction();

        WicketType wicket = null;

        switch (score) {
            case ZERO:
                wicket = onBatsmanGotOut();
                if (wicket == WicketType.NOT_OUT) {
                    log.info("Batsman does a Defense. No run.");
                }
                break;
            case RUN_ONE:
                log.info("Batsman runs a single");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                swapBatsmen();
                break;
            case LEGBY_ONE:
                log.info("Leg by. Batsman runs a single");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                swapBatsmen();
                break;
            case RUN_TWO:
                log.info("Batsman runs a double");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                break;
            case LEGBY_TWO:
                log.info("Leg by. Batsman runs a double");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                break;
            case RUN_THREE:
                log.info("Batsman runs for 3");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                swapBatsmen();
                break;
            case LEGBY_THREE:
                log.info("Leg by. Batsman runs for 3");
                log.info("Ball fielded by " + this.playInstance.getFielder().getName());
                swapBatsmen();
                break;
            case FOUR:
                log.info("Super shot. Bats hits a boundary. Crowd enjoys...");
                break;
            case SIX:
                log.info("Over boundary. Bats hits a six. Crowd roars...");
                break;
            case WIDE_BALL:
                log.info("Wide delivery by the bowler. Hitting the deck in correct line would be crucial for this game.");
                break;
            default:
                log.info("NO BALL!! Bowler won't be happy on himself. Fielding side won't afford such mistakes. Next ball would be Free hit for the batsman.");
        }
        prevScore = score;
        return Mono.just(new PlayEvent(score,wicket));
    }

    @Override
    public Mono<ScoreBoard> adjustPlayInstance(PlayEvent playEvent) {
        var scoreType = playEvent.getScoreType();
        var wicketType = playEvent.getWicketType();

        if(scoreType.getScore() > 0) {
            var run = scoreType.getScore();
            var totalScore = this.playInstance.getTotalScore();
            this.playInstance.setTotalScore(totalScore + run);
            var strikerContribution = this.playInstance.getStriker().getContribution();
            var strikerScore = strikerContribution.getScore();
            strikerContribution.setScore(strikerScore + run);
            this.playInstance.getStriker().setContribution(strikerContribution);
        }

        adjustOver(scoreType);

        return Mono.just(updateScoreBoard());
    }

    @Override
    public WicketType onBatsmanGotOut() {
        int pick = random.nextInt(WicketType.values().length);
        var wicketPicked = WicketType.values()[pick];
        if(wicketPicked == WicketType.NOT_OUT) {
            log.info("Out appeal. But Not Out decision from umpire");
        }
        if(prevScore == ScoreType.NO_BALL && wicketPicked != WicketType.NOT_OUT) {
            log.info("Feeling pity for the bowler. He took wicket in No ball. The wicket won't be counted.");
        }

        if(wicketPicked != WicketType.NOT_OUT) {

            var batsmanGotOut = this.playInstance.getStriker();

            if(wicketPicked == WicketType.BOWLED || wicketPicked == WicketType.LBW || wicketPicked == WicketType.CAUGHT_BEHIND) {
                if(wicketPicked == WicketType.BOWLED) {
                    log.info("Clean bowled!! Bowler " + this.playInstance.getBowler().getName() +
                            " jumps high into air in roar. He is charged up." + batsmanGotOut.getName() +
                            " is already walking back. Opponent supporters break into roar.");
                } else if(wicketPicked == WicketType.LBW) {
                    log.info("Loud appeal by the bowler " + this.playInstance.getBowler().getName() +
                            " and here comes umpire's finger up. The batsman is given LBW " + this.playInstance.getStriker().getName() +
                            " has to walk back to the pavilion. The ball would hit the stamps.");
                } else {
                    log.info("Loud caught behind appeal by the keeper " + this.playInstance.getWicketKeeper().getName() +
                            " and the bowler " + this.playInstance.getBowler().getName() + ". " +
                            this.playInstance.getWicketKeeper().getName() + " looks confident on his appeal and here up goes the finger from the umpire" +
                            batsmanGotOut.getName() + " has to depart. Great piece of work by " +
                            this.playInstance.getWicketKeeper().getName());
                }
                var bowlerContribution = this.playInstance.getBowler().getContribution();
                var wicketsTakenList = bowlerContribution.getWicketsTakenList();
                bowlerContribution.setWicketsTaken(bowlerContribution.getWicketsTaken() + 1);
                wicketsTakenList.add(batsmanGotOut.getName());
                bowlerContribution.setWicketsTakenList(wicketsTakenList);
                this.playInstance.getBowler().setContribution(bowlerContribution);
                log.info("Bowler " + this.playInstance.getBowler().getName() +
                        " has taken " + this.playInstance.getBowler().getContribution().getWicketsTaken() +
                        " wickets [" + this.playInstance.getBowler().getContribution().getWicketsTakenList() + "]");
            }

            if(wicketPicked == WicketType.CATCH) {
                log.info("What a catch!! " + this.playInstance.getFielder().getName() + " flies into the air to take a stunner." +
                        this.playInstance.getStriker().getName() + " can't believe it. He has to depart.");
                var fieldingContribution = this.playInstance.getFielder().getContribution();
                fieldingContribution.setCatches(fieldingContribution.getCatches() + 1);
                var catchesTakenList = fieldingContribution.getCatchList();
                catchesTakenList.add(batsmanGotOut.getName());
                fieldingContribution.setCatchList(catchesTakenList);
                this.playInstance.getFielder().setContribution(fieldingContribution);
                log.info("Fielder " + this.playInstance.getFielder().getName() +
                        " has taken " + this.playInstance.getFielder().getContribution().getCatches() +
                        " [" + this.playInstance.getFielder().getContribution().getCatchList() + "]");
            }

            if(wicketPicked == WicketType.CAUGHT_BEHIND) {
                var contribution = this.playInstance.getWicketKeeper().getContribution();
                contribution.setCatches(contribution.getCatches() + 1);
                var catchesTakenList = contribution.getCatchList();
                catchesTakenList.add(batsmanGotOut.getName());
                contribution.setCatchList(catchesTakenList);
                this.playInstance.getWicketKeeper().setContribution(contribution);
                log.info("Wicket keeper " + this.playInstance.getWicketKeeper().getName() +
                        " has taken " + this.playInstance.getWicketKeeper().getContribution().getCatches() +
                        " catches behind the stamps [" + this.playInstance.getWicketKeeper().getContribution().getCatchList() + "]");
            }

            if(wicketPicked == WicketType.RUN_OUT) {
                log.info("A clean throw from a quick pair of hands by " + this.playInstance.getFielder().getName() + ". " +
                        this.playInstance.getStriker().getName() + " hasn't reached the line yet. And he's out. He has to walk back.");
                var fieldingContribution = this.playInstance.getFielder().getContribution();
                fieldingContribution.setRunoutDone(fieldingContribution.getRunoutDone() + 1);
                var runoutDoneList = fieldingContribution.getRunoutList();
                runoutDoneList.add(batsmanGotOut.getName());
                fieldingContribution.setRunoutList(runoutDoneList);
                this.playInstance.getFielder().setContribution(fieldingContribution);
                log.info("Fielder " + this.playInstance.getFielder().getName() +
                        " has made " + this.playInstance.getFielder().getContribution().getRunoutDone() +
                        " run outs [" + this.playInstance.getFielder().getContribution().getRunoutList() + "]");
            }

            if(wicketPicked == WicketType.HIT_WICKET) {
                log.info("Goodness me!! Oh Dear.. The bat has hit the wicket and stumps fell off. Batsman " +
                        this.playInstance.getStriker().getName() + " can't believe it. He's red into anger on himself.");

            }
            log.info("Batsman " + this.playInstance.getStriker().getName() +
                    " has scored " + this.playInstance.getStriker().getContribution().getScore() + " runs");

            Batsman nextBatsman = getNextBatsman();
            log.info("Next batsman coming to the crease " + nextBatsman.getName());
            Batsman striker = nextBatsman;
            this.playInstance.setStriker(nextBatsman);
            log.info("Ready to face the ball at the striker end " + striker.getName());
            log.info("Standing at the non-striker end and is ready to run " + this.playInstance.getNonStriker().getName());

            this.playInstance.setWicketsTaken(this.playInstance.getWicketsTaken() + 1);
            log.info("Out!!! " + wicketPicked.toString());
        }

        return wicketPicked;
    }

    @Override
    public void adjustOver(ScoreType scoreType) {
        if(scoreType == ScoreType.WIDE_BALL || scoreType == ScoreType.NO_BALL) {
            return;
        }
        var ballCount = this.playInstance.getBall();
        ballCount++;

        // adjustment on over
        if(ballCount % 6 == 0) {
            var overCount = this.playInstance.getOver();
            overCount++;
            this.playInstance.setOver(overCount);
            ballCount = 0;
            swapBatsmen();
            pickCurrentBowler();
        }
        this.playInstance.setBall(ballCount);
    }

    @Override
    public ScoreBoard updateScoreBoard() {
        var scoreBoard = new ScoreBoard();
        scoreBoard.setScore(this.playInstance.getTotalScore());
        scoreBoard.setOver(this.playInstance.getOver());
        scoreBoard.setBall(this.playInstance.getBall());
        scoreBoard.setWickets(this.playInstance.getWicketsTaken());
        return scoreBoard;
    }

    public void playInit(Team battingTeam, Team fieldingTeam) {
        this.playInstance.setBattingTeam(battingTeam);
        this.playInstance.setFieldingTeam(fieldingTeam);
        log.info("Batting team " + this.playInstance.getBattingTeam().getId() + " is ready to bat whereas Fielding team " +
                this.playInstance.getFieldingTeam().getId() + " is charged up to take few early break throughs");
        this.playInstance.setTotalScore(0);
        this.playInstance.setOver(0);
        this.playInstance.setBall(0);
        this.playInstance.setStriker(getBattingTeamPlayersInBattingOrder().get(0).buildBatsman());
        this.playInstance.setNonStriker(getBattingTeamPlayersInBattingOrder().get(1).buildBatsman());
        this.playInstance.setNextBatsmanIndex(2);
        this.pickCurrentBowler();
        this.setWicketKeeper();
    }

    public Team getBattingTeamByToss() {
        int pick = random.nextInt(2);
        Team battingTeamPicked = getPlayingTeams().get(pick - 1);
        this.playInstance.setBattingTeam(battingTeamPicked);
        return battingTeamPicked;
    }

    public Team getFieldingTeam() {
        Team fieldingTeam = getPlayingTeams().stream()
                .filter(t -> !t.equals(this.playInstance.getBattingTeam()))
                .findFirst()
                .orElse(null);
        this.playInstance.setFieldingTeam(fieldingTeam);
        return fieldingTeam;
    }

    public Batsman getNextBatsman() {
        var nextBatsmanIndex = this.playInstance.getNextBatsmanIndex();
        nextBatsmanIndex++;
        this.playInstance.setNextBatsmanIndex(nextBatsmanIndex);
        return getBattingTeamPlayersInBattingOrder().get(nextBatsmanIndex).buildBatsman();
    }

    public void swapBatsmen() {
        var t = this.playInstance.getStriker();
        this.playInstance.setStriker(this.playInstance.getNonStriker());
        this.playInstance.setNonStriker(t);
    }

    public void pickCurrentBowler() {
        var availableBowlers = getFieldingTeamPlayers().stream()
                .filter(p -> p.getRoleList().contains(BOWLER) || p.getRoleList().contains(ALL_ROUNDER))
                .map(b -> b.buildBowler())
                .collect(Collectors.toList());
        var pick = random.nextInt(availableBowlers.size());
        var bowlerPicked = availableBowlers.get(pick);
        this.playInstance.setBowler(bowlerPicked);
    }

    public void setWicketKeeper() {
        var keeper = getFieldingTeamPlayers().stream()
                .filter(p -> p.getRoleList().contains(WICKETKEEPER))
                .findFirst().orElse(null)
                .buildWicketKeeper();
        this.playInstance.setWicketKeeper(keeper);
    }

    public void pickFielderInAction() {
        int pick = random.nextInt(this.getAvailableFielders().size());
        var fielderPicked = this.playInstance.getFielders().get(pick);
        this.playInstance.setFielder(fielderPicked);
    }

    public List<Fielder> getAvailableFielders() {
        List<Fielder> fielders = getFieldingTeamPlayers().stream()
                .filter(p -> !p.equals(this.playInstance.getBowler()) && !p.equals(this.playInstance.getWicketKeeper()))
                .map(f -> f.buildFielder())
                .collect(Collectors.toList());
        this.playInstance.setFielders(fielders);
        return fielders;
    }

    public List<Player> getBattingTeamPlayersInBattingOrder() {
        return this.playerReactiveRepository
                .findAllByTeamId(this.playInstance.getBattingTeam().getId())
                .collectList().block();
    }

    public List<Player> getFieldingTeamPlayers() {
        return this.playerReactiveRepository
                .findAllByTeamId(this.playInstance.getFieldingTeam().getId())
                .collectList().block();
    }

    public List<Team> getPlayingTeams() {
        var teamFlux = teamReactiveRepository.findAll();
        log.info("teamFlux = " + teamFlux);
        return teamFlux.collectList().block();
    }

    private int nextInt(int min, int max, int... exclude) {
        if(exclude == null) {
            return random.nextInt((max - min) + 1) + min;
        }
        var randInt = min - 1;
        while(!List.of(exclude).contains(randInt)) {
            randInt = random.nextInt((max - min) + 1) + min - 1;
        }
        return randInt;
    }

}