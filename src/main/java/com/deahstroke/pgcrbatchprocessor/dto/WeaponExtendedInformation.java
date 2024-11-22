package com.deahstroke.pgcrbatchprocessor.dto;

import java.util.List;

public record WeaponExtendedInformation(
    List<WeaponInformation> weapons,
    AbilityInformation values) {

}
