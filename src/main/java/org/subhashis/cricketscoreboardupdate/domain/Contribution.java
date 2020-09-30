package org.subhashis.cricketscoreboardupdate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {
    private Integer score;
    private Integer economy;
    private Integer wicketsTaken;
    private List<String> wicketsTakenList;
    private Integer catches;
    private List<String> catchList;
    private Integer runoutDone;
    private List<String> runoutList;
}
