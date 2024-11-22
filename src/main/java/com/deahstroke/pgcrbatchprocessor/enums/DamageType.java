package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum DamageType implements Labeled {

  NONE("None"),
  KINETIC("Kinetic"),
  ARC("Arc"),
  SOLAR("Solar"),
  VOID("Void"),
  STASIS("Stasis"),
  STRAND("Strand");

  @Getter
  private final String label;

  DamageType(String label) {
    this.label = label;
  }
}
