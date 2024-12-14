package com.deahstroke.pgcrbatchprocessor.enums;

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum RaidDifficulty implements Labeled {
  NORMAL("Normal"),
  MASTER("Master"),
  PRESTIGE("Prestige");

  private final String label;

  RaidDifficulty(String label) {
    this.label = label;
  }

  public static RaidDifficulty getByLabel(String label) {
    if (label.isEmpty()) {
      return NORMAL;
    } else {
      return Stream.of(RaidDifficulty.values())
          .filter(r -> r.label.equals(label))
          .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown label: " + label));
    }
  }
}
