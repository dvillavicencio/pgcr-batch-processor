package com.deahstroke.pgcrbatchprocessor.utils;

import static java.util.Map.entry;

import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class RaidUtils {

  private static final ZoneId PST = ZoneId.of("PST");
  private static final LocalTime NINE_AM = LocalTime.of(9, 0);

  private static final Map<RaidName, Instant> RELEASE_DATES = Map.ofEntries(
      entry(RaidName.LEVIATHAN,
          Instant.from(ZonedDateTime.of(LocalDate.of(2017, 9, 13), NINE_AM, PST))),
      entry(RaidName.LEVIATHAN_EATER_OF_WORLDS,
          Instant.from(ZonedDateTime.of(LocalDate.of(2017, 12, 5), NINE_AM, PST))),
      entry(RaidName.LEVIATHAN_SPIRE_OF_STARS,
          Instant.from(ZonedDateTime.of(LocalDate.of(2018, 5, 8), NINE_AM, PST))),
      entry(RaidName.LAST_WISH,
          Instant.from(ZonedDateTime.of(LocalDate.of(2018, 9, 14), NINE_AM, PST))),
      entry(RaidName.SCOURGE_OF_THE_PAST,
          Instant.from(ZonedDateTime.of(LocalDate.of(2018, 12, 7), NINE_AM, PST))),
      entry(RaidName.LEVIATHAN_CROWN_OF_SORROW,
          Instant.from(ZonedDateTime.of(LocalDate.of(2019, 6, 4), NINE_AM, PST))),
      entry(RaidName.GARDEN_OF_SALVATION,
          Instant.from(ZonedDateTime.of(LocalDate.of(2019, 10, 5), NINE_AM, PST))),
      entry(RaidName.DEEP_STONE_CRYPT,
          Instant.from(ZonedDateTime.of(LocalDate.of(2020, 11, 21), NINE_AM, PST))),
      entry(RaidName.VAULT_OF_GLASS,
          Instant.from(ZonedDateTime.of(LocalDate.of(2021, 5, 22), NINE_AM, PST))),
      entry(RaidName.VOW_OF_THE_DISCIPLE,
          Instant.from(ZonedDateTime.of(LocalDate.of(2022, 3, 5), NINE_AM, PST))),
      entry(RaidName.KINGS_FALL,
          Instant.from(ZonedDateTime.of(LocalDate.of(2022, 8, 26), NINE_AM, PST))),
      entry(RaidName.ROOT_OF_NIGHTMARES,
          Instant.from(ZonedDateTime.of(LocalDate.of(2023, 3, 10), NINE_AM, PST))),
      entry(RaidName.CROTAS_END,
          Instant.from(ZonedDateTime.of(LocalDate.of(2023, 9, 1), NINE_AM, PST))),
      entry(RaidName.SALVATIONS_EDGE,
          Instant.from(ZonedDateTime.of(LocalDate.of(2024, 6, 7), NINE_AM, PST)))
  );

  private static final Map<RaidName, Boolean> CONTEST_MODE_RAIDS = Map.ofEntries(
      entry(RaidName.LEVIATHAN, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_EATER_OF_WORLDS, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_SPIRE_OF_STARS, Boolean.FALSE),
      entry(RaidName.LAST_WISH, Boolean.FALSE),
      entry(RaidName.SCOURGE_OF_THE_PAST, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_CROWN_OF_SORROW, Boolean.FALSE),
      entry(RaidName.GARDEN_OF_SALVATION, Boolean.TRUE),
      entry(RaidName.DEEP_STONE_CRYPT, Boolean.TRUE),
      entry(RaidName.VAULT_OF_GLASS, Boolean.FALSE),
      entry(RaidName.VOW_OF_THE_DISCIPLE, Boolean.TRUE),
      entry(RaidName.KINGS_FALL, Boolean.FALSE),
      entry(RaidName.ROOT_OF_NIGHTMARES, Boolean.TRUE),
      entry(RaidName.CROTAS_END, Boolean.FALSE),
      entry(RaidName.SALVATIONS_EDGE, Boolean.TRUE)
  );

  private static final Map<RaidName, Boolean> CHALLENGE_DAY_ONE_RAIDS = Map.ofEntries(
      entry(RaidName.LEVIATHAN, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_EATER_OF_WORLDS, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_SPIRE_OF_STARS, Boolean.FALSE),
      entry(RaidName.LAST_WISH, Boolean.FALSE),
      entry(RaidName.SCOURGE_OF_THE_PAST, Boolean.FALSE),
      entry(RaidName.LEVIATHAN_CROWN_OF_SORROW, Boolean.FALSE),
      entry(RaidName.GARDEN_OF_SALVATION, Boolean.TRUE),
      entry(RaidName.DEEP_STONE_CRYPT, Boolean.FALSE),
      entry(RaidName.VAULT_OF_GLASS, Boolean.TRUE),
      entry(RaidName.VOW_OF_THE_DISCIPLE, Boolean.FALSE),
      entry(RaidName.KINGS_FALL, Boolean.FALSE),
      entry(RaidName.ROOT_OF_NIGHTMARES, Boolean.FALSE),
      entry(RaidName.CROTAS_END, Boolean.TRUE),
      entry(RaidName.SALVATIONS_EDGE, Boolean.FALSE)
  );

  private RaidUtils() {

  }

  public static Instant getRaidReleaseDate(RaidName raidName) {
    return RELEASE_DATES.get(raidName);
  }

  public static Boolean isRaidContest(RaidName raidName) {
    return CONTEST_MODE_RAIDS.get(raidName);
  }

  public static Boolean isRaidChallenge(RaidName raidName) {
    return CHALLENGE_DAY_ONE_RAIDS.get(raidName);
  }

}
