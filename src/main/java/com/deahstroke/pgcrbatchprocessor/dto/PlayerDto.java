package com.deahstroke.pgcrbatchprocessor.dto;

import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {

  private Long membershipId;

  private Integer membershipType;

  private String globalDisplayName;

  private Integer globalDisplayNameCode;

  private String displayName;

  private Set<PlayerCharacterDto> playerCharacters;

  private Set<Long> playerCharacterIds;

  /**
   * Merges this instance of a player with another instance of a player
   *
   * @param player The player to merge
   * @return The merged object
   */
  public PlayerDto merge(PlayerDto player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (!CollectionUtils.isEmpty(player.getPlayerCharacters())) {
      player.getPlayerCharacters().stream()
          .filter(p -> this.playerCharacterIds.contains(p.getCharacterId()))
          .forEach(p -> this.playerCharacters.add(p));
    }

    Optional.ofNullable(player.getGlobalDisplayName()).ifPresent(g -> this.globalDisplayName = g);
    Optional.ofNullable(player.getGlobalDisplayNameCode())
        .ifPresent(gc -> this.globalDisplayNameCode = gc);

    return this;
  }

}
