package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record EquipmentBlock(
    Long equipmentSlotTypeHash,
    Integer ammoTyper) implements Serializable {

}

