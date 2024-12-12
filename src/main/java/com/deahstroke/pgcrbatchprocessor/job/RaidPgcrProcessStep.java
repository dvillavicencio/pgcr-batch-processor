package com.deahstroke.pgcrbatchprocessor.job;

import com.deahstroke.pgcrbatchprocessor.dto.PlayerInformation;
import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.Player;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerCharacter;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStatsId;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityWeaponStats;
import com.deahstroke.pgcrbatchprocessor.entity.Raid;
import com.deahstroke.pgcrbatchprocessor.entity.RaidHash;
import com.deahstroke.pgcrbatchprocessor.entity.RaidPgcr;
import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRaidActivityStatsRepository;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRaidActivityWeaponStatsRepository;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRaidAggregateStatsRepository;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRepository;
import com.deahstroke.pgcrbatchprocessor.repository.RaidRepository;
import com.deahstroke.pgcrbatchprocessor.repository.WeaponRepository;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import jakarta.persistence.EntityManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class RaidPgcrProcessStep {

  private final PlayerRepository playerRepository;
  private final RaidRepository raidRepository;
  private final WeaponRepository weaponRepository;
  private final PlayerRaidActivityStatsRepository playerRaidActivityStatsRepository;
  private final PlayerRaidAggregateStatsRepository playerRaidAggregateStatsRepository;
  private final PlayerRaidActivityWeaponStatsRepository playerRaidActivityWeaponStatsRepository;

  public RaidPgcrProcessStep(
      PlayerRepository playerRepository,
      RaidRepository raidRepository,
      WeaponRepository weaponRepository,
      PlayerRaidActivityStatsRepository playerRaidActivityStatsRepository,
      PlayerRaidAggregateStatsRepository playerRaidAggregateStatsRepository,
      PlayerRaidActivityWeaponStatsRepository playerRaidActivityWeaponStatsRepository) {
    this.playerRepository = playerRepository;
    this.raidRepository = raidRepository;
    this.weaponRepository = weaponRepository;
    this.playerRaidActivityStatsRepository = playerRaidActivityStatsRepository;
    this.playerRaidAggregateStatsRepository = playerRaidAggregateStatsRepository;
    this.playerRaidActivityWeaponStatsRepository = playerRaidActivityWeaponStatsRepository;
  }


  @Bean
  Step raidPgcrProcessStep(JobRepository jobRepository,
      JpaTransactionManager jpaTransactionManager,
      JpaPagingItemReader<RaidPgcr> jpaPagingItemReader,
      ItemProcessor<RaidPgcr, ProcessedRaidPGCR> raidPgcrProcessor,
      ItemWriter<ProcessedRaidPGCR> raidPgcrWriter) {
    return new StepBuilder("raidPgcrProcessStep", jobRepository)
        .<RaidPgcr, ProcessedRaidPGCR>chunk(500, jpaTransactionManager)
        .reader(jpaPagingItemReader)
        .processor(raidPgcrProcessor)
        .writer(raidPgcrWriter)
        .build();
  }

  @Bean
  public JpaPagingItemReader<RaidPgcr> raidPgcrReader(EntityManagerFactory entityManagerFactory) {
    JpaPagingItemReader<RaidPgcr> jpaPagingItemReader = new JpaPagingItemReader();
    jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
    jpaPagingItemReader.setPageSize(500);
    return jpaPagingItemReader;
  }

  @Bean
  public ItemProcessor<RaidPgcr, ProcessedRaidPGCR> raidPgcrProcessor() {
    return item -> {
      try (
          ByteArrayInputStream bais = new ByteArrayInputStream(item.getBlob());
          GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
          ObjectInputStream ois = new ObjectInputStream(gzipInputStream)
      ) {
        return (ProcessedRaidPGCR) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Bean
  public ItemWriter<ProcessedRaidPGCR> raidPgcrWriter() {
    return item -> {
      for (ProcessedRaidPGCR raidPgcr : item) {
        processRaid(raidPgcr);
        raidPgcr.playerInformation().forEach(this::processPlayer);
        raidPgcr.playerInformation().forEach(p -> processPlayerStats(p, raidPgcr.instanceId()));
      }
    };
  }

  private void processRaid(ProcessedRaidPGCR pgcr) {
    Raid raidOptional = raidRepository.findByRaidName(pgcr.raidName()).orElseGet(() -> {
      Raid raid = new Raid();
      raid.setRaidDifficulty(pgcr.raidDifficulty());
      raid.setRaidName(pgcr.raidName());

      RaidName enumValue = EnumUtils.getByLabel(RaidName.class, pgcr.raidName());
      raid.setIsActive(!RaidName.SUNSET_RAIDS.contains(enumValue));

      RaidHash raidHash = new RaidHash();
      raidHash.setRaidHash(pgcr.activityHash());

      raid.addRaidHash(raidHash);
      return raid;
    });

    if (raidOptional.getRaidHash().stream()
        .noneMatch(r -> r.getRaidHash().equals(pgcr.activityHash()))) {
      RaidHash raidHash = new RaidHash();
      raidHash.setRaidHash(pgcr.activityHash());

      raidOptional.addRaidHash(raidHash);
    }
    raidRepository.save(raidOptional);
  }

  private PlayerCharacter createCharacter(PlayerInformation playerInformation,
      Player player) {
    PlayerCharacter newCharacter = new PlayerCharacter();
    newCharacter.setCharacterId(playerInformation.characterInformation().characterId());

    newCharacter.setCharacterGender(playerInformation.characterInformation().characterGender());
    newCharacter.setCharacterClass(playerInformation.characterInformation().characterClass());
    newCharacter.setCharacterRace(playerInformation.characterInformation().characterRace());
    newCharacter.setPlayer(player);
    return newCharacter;
  }

  private void processPlayer(PlayerInformation playerInformation) {
    Long membershipId = playerInformation.membershipId();
    Player player = playerRepository.findById(membershipId).orElseGet(() -> {
      Player newPlayer = new Player();
      newPlayer.setMembershipId(playerInformation.membershipId());
      newPlayer.setMembershipType(playerInformation.membershipType());
      newPlayer.setDisplayName(playerInformation.displayName());

      // Check for global display name and code in PGCR
      Optional.ofNullable(playerInformation.globalDisplayName())
          .ifPresent(newPlayer::setGlobalDisplayName);
      Optional.ofNullable(playerInformation.globalDisplayNameCode())
          .map(Integer::parseInt)
          .ifPresent(newPlayer::setGlobalDisplayNameCode);

      newPlayer.setPlayerCharacters(new HashSet<>());
      newPlayer.getPlayerCharacters().add(createCharacter(playerInformation, newPlayer));
      return newPlayer;
    });

    if (player.getPlayerCharacters().stream()
        .noneMatch(c -> c.getCharacterId()
            .equals(playerInformation.characterInformation().characterId()))) {
      PlayerCharacter newCharacter = createCharacter(playerInformation, player);
      player.getPlayerCharacters().add(newCharacter);
    }

    // Update if global display name and code are not null
    Optional.ofNullable(playerInformation.globalDisplayName())
        .ifPresent(player::setGlobalDisplayName);
    Optional.ofNullable(playerInformation.globalDisplayNameCode())
        .map(Integer::parseInt)
        .ifPresent(player::setGlobalDisplayNameCode);

    playerRepository.save(player);
  }

  private void processPlayerStats(PlayerInformation playerInformation, Long instanceId) {
    PlayerRaidActivityStats statsEntity = new PlayerRaidActivityStats();
    PlayerRaidActivityStatsId statsId = new PlayerRaidActivityStatsId(instanceId,
        playerInformation.membershipId(), playerInformation.characterInformation().characterId());
    statsEntity.setPlayerRaidActivityStatsId(statsId);
    statsEntity.setKills(playerInformation.characterInformation().kills());
    statsEntity.setDeaths(playerInformation.characterInformation().deaths());
    statsEntity.setIsCompleted(playerInformation.characterInformation().activityCompleted());
    statsEntity.setAssists(playerInformation.characterInformation().assists());

    if (playerInformation.characterInformation().kda() == null) {
      Double kda = (double) ((playerInformation.characterInformation().kills()
          + playerInformation.characterInformation().assists()) /
          playerInformation.characterInformation().assists());
      statsEntity.setKda(kda);
    } else {
      statsEntity.setKda(playerInformation.characterInformation().kda());
    }
    playerInformation.characterInformation().weaponInformation().forEach(cwe -> {

    });
  }
}
