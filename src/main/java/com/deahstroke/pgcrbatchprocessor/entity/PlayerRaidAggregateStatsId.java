package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlayerRaidAggregateStatsId {

  @Column(name = "raid_id")
  private Long raidId;

  @Column(name = "player_membership_id")
  private Long playerMembershipId;
}
