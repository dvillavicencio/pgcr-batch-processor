package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum CharacterGender implements Labeled {
  MALE("Body Type 1"),
  FEMALE("Body Type 2"),
  EMPTY("Empty");

  private final String label;

  CharacterGender(String label) {
    this.label = label;
  }
}
