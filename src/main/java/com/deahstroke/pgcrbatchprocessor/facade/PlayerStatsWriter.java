package com.deahstroke.pgcrbatchprocessor.facade;

import com.deahstroke.pgcrbatchprocessor.dto.CharacterWeaponInformation;
import com.deahstroke.pgcrbatchprocessor.dto.PlayerInformation;
import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStatsId;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityWeaponStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityWeaponStatsId;
import com.deahstroke.pgcrbatchprocessor.entity.Weapon;
import com.deahstroke.pgcrbatchprocessor.enums.EquipmentSlot;
import com.deahstroke.pgcrbatchprocessor.enums.WeaponDamageType;
import com.deahstroke.pgcrbatchprocessor.exception.ManifestException;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRaidActivityStatsRepository;
import com.deahstroke.pgcrbatchprocessor.repository.WeaponRepository;
import com.deahstroke.pgcrbatchprocessor.service.ManifestMarshallingService;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PlayerStatsWriter implements ItemWriter<ProcessedRaidPGCR> {

  private final static String BASE_URL = "https://bungie.net";

  private final ManifestMarshallingService manifestMarshallingService;
  private final WeaponRepository weaponRepository;
  private final PlayerRaidActivityStatsRepository playerRaidActivityStatsRepository;

  public PlayerStatsWriter(ManifestMarshallingService manifestMarshallingService,
      WeaponRepository weaponRepository,
      PlayerRaidActivityStatsRepository playerRaidActivityStatsRepository) {
    this.manifestMarshallingService = manifestMarshallingService;
    this.weaponRepository = weaponRepository;
    this.playerRaidActivityStatsRepository = playerRaidActivityStatsRepository;
  }

  private static Function<CharacterWeaponInformation, PlayerRaidActivityWeaponStats> createRaidActivityWeaponStat(
      PlayerInformation playerInformation, Long instanceId) {
    return wi -> {
      PlayerRaidActivityWeaponStats stats = new PlayerRaidActivityWeaponStats();
      var weaponStatsId = new PlayerRaidActivityWeaponStatsId(instanceId,
          playerInformation.characterInformation().characterId(), wi.weaponHash());
      stats.setPlayerRaidActivityWeaponStatsId(weaponStatsId);
      stats.setTotalKills(wi.kills());
      stats.setTotalPrecisionKills(wi.precisionKills());
      if (wi.precisionRatio() == null) {
        stats.setPrecisionRatio((double) (wi.precisionKills() / wi.kills()));
      } else {
        stats.setPrecisionRatio(wi.precisionRatio());
      }
      return stats;
    };
  }

  @Override
  public void write(Chunk<? extends ProcessedRaidPGCR> chunk) throws Exception {
    chunk.forEach(pgcr -> {
      var instanceId = pgcr.instanceId();
      for (PlayerInformation playerInformation : pgcr.playerInformation()) {
        PlayerRaidActivityStats statsEntity = new PlayerRaidActivityStats();
        PlayerRaidActivityStatsId statsId = new PlayerRaidActivityStatsId(instanceId,
            playerInformation.membershipId(),
            playerInformation.characterInformation().characterId());
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

        List<PlayerRaidActivityWeaponStats> weaponStats = new ArrayList<>();
        // Update weapon if any of them don't exist in DB
        if (!CollectionUtils.isEmpty(
            playerInformation.characterInformation().weaponInformation())) {
          playerInformation.characterInformation().weaponInformation().forEach(cwe -> {
            if (!weaponRepository.existsById(cwe.weaponHash())) {
              Weapon weapon = createWeapon(cwe);
              weaponRepository.save(weapon);
            }
          });
          weaponStats = playerInformation.characterInformation()
              .weaponInformation().stream()
              .map(createRaidActivityWeaponStat(playerInformation, instanceId))
              .toList();
        }
        statsEntity.setPlayerRaidActivityWeaponStats(weaponStats);
        playerRaidActivityStatsRepository.save(statsEntity);
      }
    });
  }

  private Weapon createWeapon(CharacterWeaponInformation cwe) {
    Weapon weapon = new Weapon();
    weapon.setWeaponHash(cwe.weaponHash());

    var weaponDefinition = manifestMarshallingService.getManifest(String.valueOf(cwe.weaponHash()))
        .orElseThrow(() -> new ManifestException(
            "No manifest found for id [%s]".formatted(cwe.weaponHash())));
    if (weaponDefinition.displayProperties() != null) {
      weapon.setWeaponIcon(BASE_URL + weaponDefinition.displayProperties().icon());
      weapon.setWeaponName(weaponDefinition.displayProperties().name());
    }

    if (weaponDefinition.equipmentBlock() != null) {
      weapon.setWeaponDamageType(EnumUtils.getByLabel(WeaponDamageType.class,
          String.valueOf(weaponDefinition.equipmentBlock().ammoType())));

      var equipmentSlot = manifestMarshallingService.getManifest(String.valueOf(
          weaponDefinition.equipmentBlock().equipmentSlotTypeHash())).orElseThrow(
          () -> new ManifestException("No manifest found for id [%s]".formatted(
              weaponDefinition.equipmentBlock().equipmentSlotTypeHash())));

      if (equipmentSlot.displayProperties() != null) {
        weapon.setEquipmentSlot(
            EquipmentSlot.findByLabel(equipmentSlot.displayProperties().name()));
      }
    }
    return weapon;
  }
}
