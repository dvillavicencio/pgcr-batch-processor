package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum CharacterRace implements Labeled {
  HUMAN("Human"),
  AWOKEN("Awoken"),
  EXO("Exo"),
  EMPTY("Empty");

  private final String label;

  CharacterRace(String label) {
    this.label = label;
  }
}
