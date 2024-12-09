package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player")
public class Player {

  @Id
  @EqualsAndHashCode.Include
  @Column(name = "membership_id")
  private Long membershipId;

  @Column(name = "membership_type")
  private Integer membershipType;

  @Column(name = "global_display_name")
  private String globalDisplayName;

  @Column(name = "global_display_name_code")
  private Integer globalDisplayNameCode;

  @Column(name = "display_name")
  private String displayName;

  @ToString.Exclude
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "player", orphanRemoval = true)
  private Set<PlayerCharacter> playerCharacters;

  public Player merge(Player player) {
    this.playerCharacters.addAll(player.getPlayerCharacters());
  }
}
