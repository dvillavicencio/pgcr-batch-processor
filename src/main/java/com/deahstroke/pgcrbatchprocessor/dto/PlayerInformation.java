package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record PlayerInformation(
    Long membershipId,
    Integer membershipType,
    String displayName,
    String globalDisplayName,
    String globalDisplayNameCode,
    Boolean isPublic,
    PlayerCharacterInformation characterInformation) implements Serializable {

}
