package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record CharacterWeaponInformation(
    Long weaponHash,
    Integer kills,
    Integer precisionKills,
    Double precisionRatio) implements Serializable {

}
