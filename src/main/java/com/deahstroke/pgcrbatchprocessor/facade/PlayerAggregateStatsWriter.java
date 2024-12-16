package com.deahstroke.pgcrbatchprocessor.facade;

import com.deahstroke.pgcrbatchprocessor.dto.PlayerInformation;
import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidAggregateStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidAggregateStatsId;
import com.deahstroke.pgcrbatchprocessor.entity.Raid;
import com.deahstroke.pgcrbatchprocessor.entity.RaidId;
import com.deahstroke.pgcrbatchprocessor.enums.RaidDifficulty;
import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import com.deahstroke.pgcrbatchprocessor.exception.RaidNotFoundException;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRaidAggregateStatsRepository;
import com.deahstroke.pgcrbatchprocessor.repository.RaidRepository;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import com.deahstroke.pgcrbatchprocessor.utils.RaidUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class PlayerAggregateStatsWriter implements ItemWriter<ProcessedRaidPGCR> {

  private final RaidRepository raidRepository;
  private final PlayerRaidAggregateStatsRepository playerRaidAggregateStatsRepository;

  public PlayerAggregateStatsWriter(RaidRepository raidRepository,
      PlayerRaidAggregateStatsRepository playerRaidAggregateStatsRepository) {
    this.raidRepository = raidRepository;
    this.playerRaidAggregateStatsRepository = playerRaidAggregateStatsRepository;
  }

  @Override
  public void write(Chunk<? extends ProcessedRaidPGCR> chunk) throws Exception {
    chunk.forEach(pgcr -> {
      for (PlayerInformation playerInformation : pgcr.playerInformation()) {
        RaidDifficulty difficulty = RaidDifficulty.getByLabel(pgcr.raidDifficulty());
        RaidName raidName = EnumUtils.getByLabel(RaidName.class, pgcr.raidName());
        RaidId raidId = new RaidId(raidName, difficulty);

        Raid raid = raidRepository.findById(raidId).orElseThrow(
            () -> new RaidNotFoundException("Raid with id [%s] not found".formatted(raidId)));

        PlayerRaidAggregateStatsId statsId = new PlayerRaidAggregateStatsId(raid.getRaidId()
            .getRaidName(), raid.getRaidId().getRaidDifficulty(), playerInformation.membershipId());
        PlayerRaidAggregateStats aggregateStats = playerRaidAggregateStatsRepository.findById(
                statsId)
            .orElseGet(() -> {
              var stats = new PlayerRaidAggregateStats();

              stats.setPlayerRaidAggregateStatsId(statsId);

              stats.setKills(playerInformation.characterInformation().kills());
              stats.setDeaths(playerInformation.characterInformation().deaths());
              stats.setAssists(playerInformation.characterInformation().assists());

              var hoursPlayed = Duration.between(pgcr.startTime(), pgcr.endTime()).toSeconds();
              stats.setHoursPlayed(hoursPlayed);

              var dayOne = RaidUtils.getRaidReleaseDate(raidName).plus(24,
                  ChronoUnit.HOURS);
              if (playerInformation.characterInformation().activityCompleted() &&
                  pgcr.endTime().isBefore(dayOne)) {
                stats.setDayOne(true);
              }

              if (pgcr.fromBeginning() && pgcr.flawless()
                  && playerInformation.characterInformation()
                  .activityCompleted()) {
                stats.setFlawless(true);
              }

              boolean completed = playerInformation.characterInformation().activityCompleted();
              if (completed) {
                stats.setClears(1);
              }

              if (completed && pgcr.fromBeginning()) {
                stats.setFullClears(1);
              }

              if (completed && pgcr.fromBeginning() && pgcr.trio()) {
                stats.setTrio(true);
              }

              if (completed && pgcr.fromBeginning() && pgcr.duo()) {
                stats.setDuo(true);
              }

              if (completed && pgcr.fromBeginning() && pgcr.solo()) {
                stats.setSolo(true);
              }

              if (completed && pgcr.fromBeginning() && pgcr.trio() && pgcr.flawless()) {
                stats.setTrioFlawless(true);
              }

              if (completed && pgcr.fromBeginning() && pgcr.duo() && pgcr.flawless()) {
                stats.setDuoFlawless(true);
              }

              if (completed && pgcr.fromBeginning() && pgcr.solo() && pgcr.flawless()) {
                stats.setSoloFlawless(true);
              }

              return stats;
            });
      }
    });
  }
}
