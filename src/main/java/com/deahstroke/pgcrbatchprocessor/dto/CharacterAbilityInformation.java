package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record CharacterAbilityInformation(
    Integer grenadeKills,
    Integer meleeKills,
    Integer superKills) implements Serializable {

}
