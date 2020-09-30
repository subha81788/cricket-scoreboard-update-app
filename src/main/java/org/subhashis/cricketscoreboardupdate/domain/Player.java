package org.subhashis.cricketscoreboardupdate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.subhashis.cricketscoreboardupdate.enums.Role;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"id", "teamId"})
public class Player {
    @Id
    private String id;
    private String teamId;
    private String name;
    private List<Role> roleList;
    private Contribution contribution;

    public final Batsman buildBatsman() {
        var batsman = new Batsman();
        batsman.setId(this.id);
        batsman.setTeamId(this.teamId);
        batsman.setName(this.name);
        batsman.setRoleList(this.roleList);
        batsman.setContribution(this.contribution);
        return batsman;
    }

    public final Bowler buildBowler() {
        var bowler = new Bowler();
        bowler.setId(this.id);
        bowler.setTeamId(this.teamId);
        bowler.setName(this.name);
        bowler.setRoleList(this.roleList);
        bowler.setContribution(this.contribution);
        return bowler;
    }

    public final WicketKeeper buildWicketKeeper() {
        var keeper = new WicketKeeper();
        keeper.setId(this.id);
        keeper.setTeamId(this.teamId);
        keeper.setName(this.name);
        keeper.setRoleList(this.roleList);
        keeper.setContribution(this.contribution);
        return keeper;
    }

    public final Fielder buildFielder() {
        var fielder = new Fielder();
        fielder.setId(this.id);
        fielder.setTeamId(this.teamId);
        fielder.setName(this.name);
        fielder.setRoleList(this.roleList);
        fielder.setContribution(this.contribution);
        return fielder;
    }
}
