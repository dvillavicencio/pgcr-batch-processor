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
@ToString
@EqualsAndHashCode
public class PlayerRaidActivityWeaponStatsId {

  @Column(name = "instance_id")
  private Long instanceId;

  @Column(name = "player_character_id")
  private Long playerCharacterId;

  @Column(name = "weapon_id")
  private Long weaponId;

}
