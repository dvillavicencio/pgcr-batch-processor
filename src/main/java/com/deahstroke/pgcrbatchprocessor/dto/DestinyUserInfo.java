package com.deahstroke.pgcrbatchprocessor.dto;

public record DestinyUserInfo(
    String iconPath,
    Integer membershipType,
    Long membershipId,
    String displayName,
    Boolean isPublic,
    String bungieGlobalDisplayName,
    String bungieGlobalDisplayNameCode) {

}
