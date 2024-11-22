package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record PostGameCarnageReport(
    Instant period,
    Boolean activityWasStartedFromBeginning,
    ActivityDetails activityDetails,
    List<PostGameCarnageReportEntry> entries) implements Serializable {

  /**
   * Fall back Post Game Carnage Report for empty responses or for big responses (> 16KB)
   */
  public static final PostGameCarnageReport EMPTY_RESPONSE = new PostGameCarnageReport(
      Instant.now(Clock.systemUTC()),
      false,
      null,
      Collections.emptyList());
}
