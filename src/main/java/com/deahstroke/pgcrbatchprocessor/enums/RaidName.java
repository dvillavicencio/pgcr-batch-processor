package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

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
  LEVIATHAN_SPIRE_OF_STARS("Leviathan: Spire of Stars"),
  LEVIATHAN_EATER_OF_WORLDS("Leviathan: Eater of Worlds"),
  LEVIATHAN("Leviathan");

  @Getter
  private final String label;

  RaidName(String label) {
    this.label = label;
  }
}
