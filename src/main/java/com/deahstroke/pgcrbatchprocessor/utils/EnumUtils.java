package com.deahstroke.pgcrbatchprocessor.utils;

import com.deahstroke.pgcrbatchprocessor.enums.Labeled;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class EnumUtils {

  private EnumUtils() {
  }

  public static <T extends Enum<T> & Labeled> T getByLabel(Class<T> enumClazz, String label) {
    return Stream.of(enumClazz.getEnumConstants())
        .filter(l -> l.getLabel().equals(label))
        .findFirst()
        .orElseThrow(() -> {
          String errorMessage = "No such element for label [%s] and enum class [%s]";
          throw new NoSuchElementException(errorMessage.formatted(label, enumClazz.getName()));
        });
  }
}
