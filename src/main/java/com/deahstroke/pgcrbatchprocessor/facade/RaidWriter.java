package com.deahstroke.pgcrbatchprocessor.facade;

import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.Raid;
import com.deahstroke.pgcrbatchprocessor.entity.RaidHash;
import com.deahstroke.pgcrbatchprocessor.entity.RaidId;
import com.deahstroke.pgcrbatchprocessor.enums.RaidDifficulty;
import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import com.deahstroke.pgcrbatchprocessor.repository.RaidRepository;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class RaidWriter implements ItemWriter<ProcessedRaidPGCR> {

  private final RaidRepository raidRepository;

  public RaidWriter(RaidRepository raidRepository) {
    this.raidRepository = raidRepository;
  }

  @Override
  public void write(Chunk<? extends ProcessedRaidPGCR> chunk) throws Exception {
    chunk.forEach(pgcr -> {
      RaidDifficulty difficulty = RaidDifficulty.getByLabel(pgcr.raidDifficulty());
      RaidName raidName = EnumUtils.getByLabel(RaidName.class, pgcr.raidName());
      RaidId raidId = new RaidId(raidName, difficulty);

      Raid raidOptional = raidRepository.findById(raidId).orElseGet(() -> {
        Raid raid = new Raid();

        raid.setRaidId(raidId);
        raid.setIsActive(!RaidName.SUNSET_RAIDS.contains(raidName));

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
    });
  }
}
