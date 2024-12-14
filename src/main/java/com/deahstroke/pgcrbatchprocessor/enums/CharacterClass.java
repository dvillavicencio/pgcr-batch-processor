package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum CharacterClass implements Labeled {
  TITAN("Titan"),
  WARLOCK("Warlock"),
  HUNTER("Hunter"),
  EMPTY("Empty");

  private final String label;

  CharacterClass(String label) {
    this.label = label;
  }
}
