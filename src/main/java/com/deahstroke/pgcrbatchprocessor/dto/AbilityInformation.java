package com.deahstroke.pgcrbatchprocessor.dto;

import com.deahstroke.pgcrbatchprocessor.deserializers.WrapperOrBasicDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record AbilityInformation(
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object precisionKills,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object weaponKillsGrenade,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object weaponKillsMelee,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object weaponKillsSuper,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object weaponKillsAbility) {

}
