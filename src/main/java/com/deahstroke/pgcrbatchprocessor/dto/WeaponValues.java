package com.deahstroke.pgcrbatchprocessor.dto;

import com.deahstroke.pgcrbatchprocessor.deserializers.WrapperOrBasicDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record WeaponValues(
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object uniqueWeaponKills,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object uniqueWeaponPrecisionKills,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object uniqueWeaponKillsPrecisionKills) {

}
