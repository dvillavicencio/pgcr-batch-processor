package com.deahstroke.pgcrbatchprocessor.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EquipmentSlot implements Labeled {
  PRIMARY("Kinetic Weapons"),
  SPECIAL("Energy Weapons"),
  HEAVY("Power Weapons");

  private final String label;

  EquipmentSlot(String label) {
    this.label = label;
  }

  public static EquipmentSlot findByLabel(String label) {
    return Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.label.equals(label))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No equipment slot with label " + label));
  }
}
