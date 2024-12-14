package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum WeaponDamageType implements Labeled {
  ARC("2"),
  VOID("4"),
  SOLAR("3"),
  STASIS("6"),
  STRAND("7"),
  KINETIC("1");

  private final String label;

  WeaponDamageType(String label) {
    this.label = label;
  }
}
