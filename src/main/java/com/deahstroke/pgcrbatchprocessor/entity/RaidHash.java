package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Table(name = "raid_hash")
public class RaidHash {

  @Id
  @Column(name = "raid_hash_id")
  private Long id;

  @Column(name = "raid_id")
  private Long raidId;

  @Column(name = "raid_hash")
  private Long raidHash;

  @ManyToOne(fetch = FetchType.LAZY)
  private Raid raid;

}
