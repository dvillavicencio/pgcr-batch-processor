package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "player_raid_aggregate_stats")
public class PlayerRaidAggregateStats {

  @EmbeddedId
  private PlayerRaidAggregateStatsId playerRaidAggregateStatsId;

  @Column(name = "kills")
  private Integer kills;

  @Column(name = "deaths")
  private Integer deaths;

  @Column(name = "assists")
  private Integer assists;

  @Column(name = "hour_played")
  private Integer hoursPlayed;

  @Column(name = "clears")
  private Integer clears;

  @Column(name = "full_clears")
  private Integer fullClears;

  @Column(name = "flawless")
  private Boolean flawless;

  @Column(name = "contest_clear")
  private Boolean contestClear;

  @Column(name = "day_one")
  private Boolean dayOne;

  @Column(name = "solo")
  private Boolean solo;

  @Column(name = "duo")
  private Boolean duo;

  @Column(name = "trio")
  private Boolean trio;

  @Column(name = "solo_flawless")
  private Boolean soloFlawless;

  @Column(name = "duo_flawless")
  private Boolean duoFlawless;

  @Column(name = "trio_flawless")
  private Boolean trioFlawless;
}
