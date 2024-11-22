package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public record ProcessedRaidPGCR(
    Instant startTime,
    Instant endTime,
    Boolean fromBeginning,
    Long instanceId,
    String raidName,
    String raidDifficulty,
    Long activityHash,
    Boolean flawless,
    Boolean solo,
    Boolean duo,
    Boolean trio,
    Set<PlayerInformation> playerInformation) implements Serializable {

}
