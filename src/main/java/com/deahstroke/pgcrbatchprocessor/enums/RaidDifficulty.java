package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum RaidDifficulty implements Labeled {
  NORMAL("Normal"),
  MASTER("Master"),
  PRESTIGE("Prestige");

  @Getter
  private final String label;

  RaidDifficulty(String label) {
    this.label = label;
  }


}
