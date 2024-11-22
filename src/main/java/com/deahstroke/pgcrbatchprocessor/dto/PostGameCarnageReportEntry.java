package com.deahstroke.pgcrbatchprocessor.dto;

public record PostGameCarnageReportEntry(
    Integer standing,
    PlayerEntry player,
    PlayerStatsValues values,
    WeaponExtendedInformation extended,
    Long characterId) {

}
