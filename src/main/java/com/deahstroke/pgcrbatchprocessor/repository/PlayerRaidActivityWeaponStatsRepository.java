package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityWeaponStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityWeaponStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRaidActivityWeaponStatsRepository extends
    JpaRepository<PlayerRaidActivityWeaponStats, PlayerRaidActivityWeaponStatsId> {

}
