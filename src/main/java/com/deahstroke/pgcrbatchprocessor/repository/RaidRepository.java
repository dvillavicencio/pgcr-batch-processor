package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.Raid;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaidRepository extends JpaRepository<Raid, Long> {

  boolean existsByRaidNameIgnoreCase(String raidName);

  Optional<Raid> findByRaidName(String raidName);
}
