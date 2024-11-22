package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum CharacterClass implements Labeled {
  Titan("Titan"),
  Warlock("Warlock"),
  Hunter("Hunter"),
  Empty("Empty");

  @Getter
  private final String label;

  CharacterClass(String label) {
    this.label = label;
  }
}
