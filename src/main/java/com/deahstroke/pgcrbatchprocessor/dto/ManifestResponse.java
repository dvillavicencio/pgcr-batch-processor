package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record ManifestResponse(
    DisplayProperties displayProperties,
    EquipmentBlock equipmentBlock,
    Long defaultDamageType,
    Integer itemType,
    Integer itemSubType,
    Integer directActivityModeType,
    Long activityTypeHash,
    Long hash) implements Serializable {

}
