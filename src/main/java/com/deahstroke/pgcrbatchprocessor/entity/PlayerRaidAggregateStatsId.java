package com.deahstroke.pgcrbatchprocessor.entity;

import com.deahstroke.pgcrbatchprocessor.enums.RaidDifficulty;
import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlayerRaidAggregateStatsId {

  @Column(name = "raid_name")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private RaidName raidName;

  @Column(name = "raid_difficulty")
  private RaidDifficulty raidDifficulty;

  @Column(name = "player_membership_id")
  private Long playerMembershipId;
}
