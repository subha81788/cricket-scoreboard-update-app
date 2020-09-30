package org.subhashis.cricketscoreboardupdate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fielder extends Player {
    private String position;
    private Boolean isWicketKeeper;
}
