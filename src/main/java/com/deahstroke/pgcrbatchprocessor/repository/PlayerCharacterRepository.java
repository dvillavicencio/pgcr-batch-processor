package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.PlayerCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacter, Long> {

  @Modifying
  @Query(value =
      "INSERT INTO player_character (character_id, character_class, character_race, character_gender, current_emblem, player_membership_id) "
          + "VALUES (:#{#playerCharacter.characterId}, CAST(:#{#playerCharacter.characterClass.name()} AS CHARACTER_CLASS), "
          + "CAST(:#{#playerCharacter.characterRace.name()} AS CHARACTER_RACE), "
          + "CAST(:#{#playerCharacter.characterGender.name()} AS CHARACTER_GENDER), "
          + ":#{#playerCharacter.currentEmblem}, :#{#playerCharacter.player.membershipId}) "
          + "ON CONFLICT (character_id) DO UPDATE SET "
          + "character_class = EXCLUDED.character_class, "
          + "character_race = EXCLUDED.character_race, "
          + "character_gender = EXCLUDED.character_gender, "
          + "current_emblem = EXCLUDED.current_emblem, "
          + "player_membership_id = EXCLUDED.player_membership_id",
      nativeQuery = true)
  void insertOnConflict(PlayerCharacter playerCharacter);
}
