package com.deahstroke.pgcrbatchprocessor.dto;

import java.util.List;


public record ActivityDetails(
    Long referenceId,
    Long directorActivityHash,
    String instanceId,
    Integer mode,
    List<Integer> modes,
    Boolean isPrivate,
    Integer membershipType) {

}
