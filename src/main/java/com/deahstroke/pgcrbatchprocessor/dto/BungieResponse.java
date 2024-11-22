package com.deahstroke.pgcrbatchprocessor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record BungieResponse<T>(
    @JsonAlias("Response") T response,
    @JsonAlias("ErrorCode") Integer errorCode) {

}
