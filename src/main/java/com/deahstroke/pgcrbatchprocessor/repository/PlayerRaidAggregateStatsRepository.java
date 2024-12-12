package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidAggregateStats;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerRaidAggregateStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRaidAggregateStatsRepository extends
    JpaRepository<PlayerRaidAggregateStats, PlayerRaidAggregateStatsId> {

}
