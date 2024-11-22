package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

public enum EquipmentSlot implements Labeled {
  KINETIC_WEAPONS("Kinetic Weapons"),
  ENERGY_WEAPONS("Energy Weapons"),
  POWER_WEAPONS("Power Weapons");

  @Getter
  private final String label;

  EquipmentSlot(String label) {
    this.label = label;
  }
}
