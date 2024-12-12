package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
@EqualsAndHashCode
@ToString
@Table(name = "raid")
public class Raid {

  @Id
  @Column(name = "raid_id")
  private Long id;

  @Column(name = "raid_name")
  private String raidName;

  @Column(name = "raid_difficulty")
  private String raidDifficulty;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "release_date")
  private Instant releaseDate;

  @ToString.Exclude
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "raid")
  Set<RaidHash> raidHash = new HashSet<>();

  public void addRaidHash(RaidHash raidHash) {
    this.raidHash.add(raidHash);
    raidHash.setRaid(this);
  }
}
