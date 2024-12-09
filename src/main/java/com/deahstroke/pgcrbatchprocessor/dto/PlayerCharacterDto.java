package com.deahstroke.pgcrbatchprocessor.dto;

import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCharacterDto {

  private Long characterId;

  private CharacterClass characterClass;

  private CharacterRace characterRace;

  private CharacterGender characterGender;

  private String currentEmblem;
}
