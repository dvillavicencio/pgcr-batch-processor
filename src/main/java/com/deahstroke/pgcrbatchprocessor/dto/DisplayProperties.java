package com.deahstroke.pgcrbatchprocessor.dto;

import java.io.Serializable;

public record DisplayProperties(
    String description,
    String name,
    String icon,
    String highResIcon,
    Boolean hasIcon) implements Serializable {

}
