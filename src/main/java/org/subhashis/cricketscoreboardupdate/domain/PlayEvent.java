package org.subhashis.cricketscoreboardupdate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.subhashis.cricketscoreboardupdate.enums.ScoreType;
import org.subhashis.cricketscoreboardupdate.enums.WicketType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayEvent {
    private ScoreType scoreType;
    private WicketType wicketType;
}
