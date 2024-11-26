package com.deahstroke.pgcrbatchprocessor.processor;

import com.deahstroke.pgcrbatchprocessor.dto.Basic;
import com.deahstroke.pgcrbatchprocessor.dto.CharacterAbilityInformation;
import com.deahstroke.pgcrbatchprocessor.dto.CharacterWeaponInformation;
import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import com.deahstroke.pgcrbatchprocessor.dto.PlayerCharacterInformation;
import com.deahstroke.pgcrbatchprocessor.dto.PlayerInformation;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReportEntry;
import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PgcrProcessor implements
    ItemProcessor<PostGameCarnageReport, ProcessedRaidPGCR> {

  private static final Long SOTP_HASH_1 = 548750096L;
  private static final Long SOTP_HASH_2 = 2812525063L;

  private static final Instant BEYOND_LIGHT_RELEASE = Instant.from(ZonedDateTime.of(
      LocalDate.of(2020, 11, 10),
      LocalTime.of(9, 0, 0),
      ZoneId.of("America/Los_Angeles"))
  );
  private static final Instant WITCH_QUEEN_RELEASE = Instant.from(ZonedDateTime.of(
      LocalDate.of(2022, 2, 22),
      LocalTime.of(9, 0, 0),
      ZoneId.of("America/Los_Angeles")
  ));
  private static final Instant SEASON_OF_HAUNTED_RELEASE = Instant.from(ZonedDateTime.of(
      LocalDate.of(2022, 5, 24),
      LocalTime.of(9, 0, 0),
      ZoneId.of("America/Los_Angeles")
  ));

  private static final Set<Long> LEVI_HASHES = Set.of(
      2693136600L, 2693136601L, 2693136602L,
      2693136603L, 2693136604L, 2693136605L,
      89727599L, 287649202L, 1699948563L, 1875726950L,
      3916343513L, 4039317196L, 417231112L, 508802457L,
      757116822L, 771164842L, 1685065161L, 1800508819L,
      2449714930L, 3446541099L, 4206123728L, 3912437239L,
      3879860661L, 3857338478L
  );

  private final Counter raidPgcrCounter;
  private final Counter nonRaidPgcrCounter;
  private final RedisTemplate<String, ManifestResponse> redisTemplate;

  public PgcrProcessor(
      MeterRegistry meterRegistry,
      RedisTemplate<String, ManifestResponse> redisTemplate) {
    raidPgcrCounter = Counter.builder("raid_pgcr_count")
        .description("Number of PGCRs that are Raid PGCRs, e.g., type 4 in the API")
        .register(meterRegistry);
    nonRaidPgcrCounter = Counter.builder("non_raid_pgcr_counter")
        .description("Number of PGCRs that are non-Raid PGCRs, e.g., no type 4")
        .register(meterRegistry);
    this.redisTemplate = redisTemplate;
  }

  /**
   * Figure out if the activity was started from the beginning. There's additional logic to this
   * because of how PGCRs have changed over time. Credits to Newo for explaining this to me
   *
   * @param item the PGCR to process
   * @return true or false
   */
  private static Boolean resolveFromBeginning(PostGameCarnageReport item, Boolean flawless) {
    if (item.period().isAfter(SEASON_OF_HAUNTED_RELEASE)) {
      return item.activityWasStartedFromBeginning();
    } else if (item.period().isBefore(BEYOND_LIGHT_RELEASE)) {
      if (item.startingPhaseIndex() == null) {
        return false;
      }
      if (item.activityDetails().directorActivityHash().equals(SOTP_HASH_1)
          || item.activityDetails().directorActivityHash().equals(SOTP_HASH_2)) {
        return item.startingPhaseIndex() <= 1;
      } else if (LEVI_HASHES.contains(item.activityDetails().directorActivityHash())) {
        return item.startingPhaseIndex() == 0 || item.startingPhaseIndex() == 1;
      } else {
        return item.startingPhaseIndex() == 0;
      }
    } else if (item.period().isAfter(WITCH_QUEEN_RELEASE) && (item.activityWasStartedFromBeginning()
        || flawless)) {
      return item.activityWasStartedFromBeginning();
    } else {
      return false;
    }
  }

  @Override
  public ProcessedRaidPGCR process(PostGameCarnageReport item) {
    if (item.activityDetails().mode() != 4) {
      nonRaidPgcrCounter.increment();
      return null;
    }

    Instant startTime = item.period();
    int activityDuration =
        (item.entries().get(0).values().activityDurationSeconds() instanceof Basic duration)
            ? Integer.parseInt(duration.displayValue())
            : (Integer) item.entries().get(0).values().activityDurationSeconds();
    Instant endTime = item.entries().isEmpty() ? startTime :
        startTime.plus(Duration.ofSeconds(activityDuration));
    Long instanceId = Long.valueOf(item.activityDetails().instanceId());

    ManifestResponse manifestResponse = redisTemplate.opsForValue()
        .get(String.valueOf(item.activityDetails().directorActivityHash()));

    String raidName = "";
    String raidDifficulty = "";
    if (!Objects.isNull(manifestResponse) && !Objects.isNull(
        manifestResponse.displayProperties())) {
      String[] tokens = manifestResponse.displayProperties().name().split(":");
      raidName = tokens[0].trim();
      raidDifficulty = tokens.length > 1 ? tokens[1].trim() : "";
    }
    Long activityHash = item.activityDetails().directorActivityHash();

    Set<PlayerInformation> players = new HashSet<>(item.entries().size());
    if (!item.entries().isEmpty()) {
      players = item.entries().stream()
          .map(entry -> {
            Long membershipId = entry.player().destinyUserInfo().membershipId();
            Integer membershipType = entry.player().destinyUserInfo().membershipType();
            String displayName = entry.player().destinyUserInfo().displayName();
            String playerName = entry.player().destinyUserInfo().bungieGlobalDisplayName();
            String playerTag = entry.player().destinyUserInfo().bungieGlobalDisplayNameCode();
            Boolean isPublic = entry.player().destinyUserInfo().isPrivate();

            PlayerCharacterInformation characterInfo = createPlayerInformation(entry);
            return new PlayerInformation(membershipId, membershipType, displayName, playerName,
                playerTag, isPublic, characterInfo);
          }).collect(Collectors.toSet());
    }

    var flawless = players.stream()
        .allMatch(player -> player.characterInformation().deaths() == 0);
    var uniquePlayerCount = players.stream()
        .map(PlayerInformation::membershipId)
        .distinct()
        .count();
    var trio = uniquePlayerCount == 3;
    var duo = uniquePlayerCount == 2;
    var solo = uniquePlayerCount == 1;

    Boolean fromBeginning = resolveFromBeginning(item, flawless);
    raidPgcrCounter.increment();
    return new ProcessedRaidPGCR(startTime, endTime, fromBeginning,
        instanceId, raidName, raidDifficulty, activityHash, flawless, solo, duo, trio, players);
  }

  private PlayerCharacterInformation createPlayerInformation(PostGameCarnageReportEntry entry) {
    Long characterId = entry.characterId();
    Integer lightLevel = entry.player().lightLevel();

    ManifestResponse manifestClass = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().classHash()));
    ManifestResponse manifestGender = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().genderHash()));
    ManifestResponse manifestRace = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().raceHash()));

    CharacterClass characterClazz = EnumUtils.getByLabel(CharacterClass.class,
        manifestClass == null || manifestClass.displayProperties() == null ? "Empty"
            : manifestClass.displayProperties().name());
    CharacterGender characterGender = EnumUtils.getByLabel(CharacterGender.class,
        manifestGender == null || manifestGender.displayProperties() == null ? "Empty" :
            manifestGender.displayProperties().name());
    CharacterRace characterRace = EnumUtils.getByLabel(CharacterRace.class,
        manifestRace == null || manifestRace.displayProperties() == null ? "Empty" :
            manifestRace.displayProperties().name());

    // Stats
    Boolean activityCompleted =
        entry.values().completed() instanceof Basic basic && basic.value() > 0;
    Integer kills = (entry.values().kills() instanceof Basic basic) ?
        Integer.parseInt(basic.displayValue()) : (Integer) entry.values().kills();
    Integer deaths = (entry.values().deaths() instanceof Basic basic) ?
        Integer.parseInt(basic.displayValue()) : (Integer) entry.values().deaths();
    Integer assists = (entry.values().assists() instanceof Basic basic) ?
        Integer.parseInt(basic.displayValue()) : (Integer) entry.values().assists();
    Duration timePlayed = (entry.values().activityDurationSeconds() instanceof Basic basic) ?
        Duration.ofSeconds(Math.round(basic.value()))
        : Duration.ofSeconds((Integer) entry.values().activityDurationSeconds());

    List<CharacterWeaponInformation> weaponInformation;
    if (Objects.isNull(entry.extended()) || CollectionUtils.isEmpty(entry.extended().weapons())) {
      weaponInformation = Collections.emptyList();
    } else {
      weaponInformation = entry.extended().weapons().stream()
          .map(weapon -> {
            Integer weaponKills = weapon.values().uniqueWeaponKills() instanceof Basic basic ?
                Integer.parseInt(basic.displayValue())
                : (Integer) weapon.values().uniqueWeaponKills();
            Integer precisionKills =
                weapon.values().uniqueWeaponPrecisionKills() instanceof Basic basic ?
                    Integer.parseInt(basic.displayValue())
                    : (Integer) weapon.values().uniqueWeaponPrecisionKills();
            return new CharacterWeaponInformation(weapon.referenceId(), weaponKills, precisionKills,
                null);
          }).toList();
    }

    int grenadeKills = 0;
    if (Objects.nonNull(entry.extended()) &&
        Objects.nonNull(entry.extended().values().weaponKillsGrenade())) {
      grenadeKills = entry.extended().values().weaponKillsGrenade() instanceof Basic basic ?
          Integer.parseInt(basic.displayValue())
          : (Integer) entry.extended().values().weaponKillsGrenade();
    }
    int superKills = 0;
    if (Objects.nonNull(entry.extended()) &&
        Objects.nonNull(entry.extended().values().weaponKillsSuper())) {
      superKills = entry.extended().values().weaponKillsSuper() instanceof Basic basic ?
          Integer.parseInt(basic.displayValue())
          : (Integer) entry.extended().values().weaponKillsSuper();
    }
    int meleeKills = 0;
    if (Objects.nonNull(entry.extended()) &&
        Objects.nonNull(entry.extended().values().weaponKillsMelee())) {
      meleeKills = entry.extended().values().weaponKillsMelee() instanceof Basic basic ?
          Integer.parseInt(basic.displayValue())
          : (Integer) entry.extended().values().weaponKillsMelee();
    }
    CharacterAbilityInformation abilityInformation = new CharacterAbilityInformation(grenadeKills,
        meleeKills, superKills);
    return new PlayerCharacterInformation(characterId,
        lightLevel, characterClazz, characterRace, characterGender, entry.player().emblemHash(),
        activityCompleted, kills, assists, deaths, null, null, timePlayed, weaponInformation,
        abilityInformation);
  }
}
