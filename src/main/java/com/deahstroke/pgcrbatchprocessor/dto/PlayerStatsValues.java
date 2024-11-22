package com.deahstroke.pgcrbatchprocessor.dto;

import com.deahstroke.pgcrbatchprocessor.deserializers.WrapperOrBasicDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record PlayerStatsValues(
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object assists,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object completed,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object deaths,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object kills,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object opponentsDefeated,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object efficiency,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object killsDeathsRatio,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object killsDeathsAssists,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object score,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object activityDurationSeconds,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object completionReason,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object startSeconds,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object timePlayedSeconds,
    @JsonDeserialize(using = WrapperOrBasicDeserializer.class)
    Object playerCount) {

}
