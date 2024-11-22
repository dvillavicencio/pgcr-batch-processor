package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum CharacterGender implements Labeled {
  Male("Body Type 1"),
  Female("Body Type 2"),
  Empty("Empty");

  @Getter
  private final String label;

  CharacterGender(String label) {
    this.label = label;
  }
}
