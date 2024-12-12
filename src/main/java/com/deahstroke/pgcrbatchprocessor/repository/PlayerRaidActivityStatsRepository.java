package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidActivityStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRaidActivityStatsRepository extends
    JpaRepository<PlayerRaidActivityStats, PlayerRaidActivityStatsId> {

}
