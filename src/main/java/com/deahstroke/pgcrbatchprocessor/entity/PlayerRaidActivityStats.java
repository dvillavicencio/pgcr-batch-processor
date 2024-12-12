package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "player_raid_activity_stats")
public class PlayerRaidActivityStats {

  @EmbeddedId
  PlayerRaidActivityStatsId playerRaidActivityStatsId;

  @Column(name = "is_completed")
  private Boolean isCompleted;

  @Column(name = "kills")
  private Integer kills;

  @Column(name = "deaths")
  private Integer deaths;

  @Column(name = "assists")
  private Integer assists;

  @Column(name = "killsdeathsassists")
  private Double kda;

  @Column(name = "duration_seconds")
  private Integer durationSeconds;

  @Column(name = "time_played_seconds")
  private Integer timePlayedSeconds;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "")
  private List<PlayerRaidActivityWeaponStats> playerRaidActivityWeaponStats;
}
