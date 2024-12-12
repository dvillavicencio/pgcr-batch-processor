package com.deahstroke.pgcrbatchprocessor.enums;

import lombok.Getter;

@Getter
public enum EquipmentSlot implements Labeled {
  Primary("Primary Weapons"),
  Special("Energy Weapons"),
  Heavy("Power Weapons");

  private final String label;

  EquipmentSlot(String label) {
    this.label = label;
  }
}
