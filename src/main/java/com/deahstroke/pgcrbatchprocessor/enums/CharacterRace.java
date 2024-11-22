package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum CharacterRace implements Labeled {
  Human("Human"),
  Awoken("Awoken"),
  Exo("Exo"),
  Empty("Empty");

  @Getter
  private final String label;

  CharacterRace(String label) {
    this.label = label;
  }
}
