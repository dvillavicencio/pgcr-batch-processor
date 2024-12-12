package com.deahstroke.pgcrbatchprocessor.enums;

import java.util.Set;
import lombok.Getter;

@Getter
public enum RaidName implements Labeled {
  SALVATIONS_EDGE("Salvation's Edge"),
  CROTAS_END("Crota's End"),
  ROOT_OF_NIGHTMARES("Root of Nightmares"),
  KINGS_FALL("King's Fall"),
  VOW_OF_THE_DISCIPLE("Vow of the Disciple"),
  VAULT_OF_GLASS("Vault of Glass"),
  DEEP_STONE_CRYPT("Deep Stone Crypt"),
  GARDEN_OF_SALVATION("Garden of Salvation"),
  LEVIATHAN_CROWN_OF_SORROW("Leviathan: Crown of Sorrow"),
  LAST_WISH("Last Wish"),
  SCOURGE_OF_THE_PAST("Scourge of the Past"),
  LEVIATHAN_SPIRE_OF_STARS("Leviathan: Spire of Stars"),
  LEVIATHAN_EATER_OF_WORLDS("Leviathan: Eater of Worlds"),
  LEVIATHAN("Leviathan");

  public static Set<RaidName> SUNSET_RAIDS = Set.of(
      LEVIATHAN, LEVIATHAN_EATER_OF_WORLDS, LEVIATHAN_CROWN_OF_SORROW, LEVIATHAN_SPIRE_OF_STARS,
      SCOURGE_OF_THE_PAST
  );
  private final String label;

  RaidName(String label) {
    this.label = label;
  }
}
