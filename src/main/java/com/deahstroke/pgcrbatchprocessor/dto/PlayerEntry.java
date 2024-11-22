package com.deahstroke.pgcrbatchprocessor.dto;

public record PlayerEntry(
    DestinyUserInfo destinyUserInfo,
    String characterClass,
    Long classHash,
    Long raceHash,
    Long genderHash,
    Long emblemHash,
    Integer lightLevel
) {

}
