package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "player_raid_activity_weapon_stats")
public class PlayerRaidActivityWeaponStats {

  @EmbeddedId
  private PlayerRaidActivityWeaponStatsId playerRaidActivityWeaponStatsId;

  @Column(name = "total_kills")
  private Integer totalKills;

  @Column(name = "total_precision_kills")
  private Integer totalPrecisionKills;

  @Column(name = "precision_ratio")
  private Double precisionRatio;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private PlayerRaidActivityStats playerRaidActivityStats;
}
