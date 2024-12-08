package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum CharacterClass implements Labeled {
  Titan("Titan"),
  Warlock("Warlock"),
  Hunter("Hunter"),
  Empty("Empty");

  private final String label;

  CharacterClass(String label) {
    this.label = label;
  }
}
