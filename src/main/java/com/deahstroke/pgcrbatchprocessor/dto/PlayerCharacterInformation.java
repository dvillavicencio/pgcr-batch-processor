package com.deahstroke.pgcrbatchprocessor.dto;

import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;

public record PlayerCharacterInformation(
    Long characterId,
    Integer lightLevel,
    CharacterClass characterClass,
    CharacterRace characterRace,
    CharacterGender characterGender,
    Long characterEmblem,
    Boolean activityCompleted,
    Integer kills,
    Integer assists,
    Integer deaths,
    Double kda,
    Double kdr,
    Duration timePlayed,
    List<CharacterWeaponInformation> weaponInformation,
    CharacterAbilityInformation abilityInformation) implements Serializable {

}
