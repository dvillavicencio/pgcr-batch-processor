package com.deahstroke.pgcrbatchprocessor.facade;

import com.deahstroke.pgcrbatchprocessor.dto.PlayerInformation;
import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.Player;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerCharacter;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRepository;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class PlayerWriter implements ItemWriter<ProcessedRaidPGCR> {

  private final PlayerRepository playerRepository;

  public PlayerWriter(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  private PlayerCharacter createCharacter(PlayerInformation playerInformation,
      Player player) {
    PlayerCharacter newCharacter = new PlayerCharacter();
    newCharacter.setCharacterId(playerInformation.characterInformation().characterId());

    newCharacter.setCharacterGender(playerInformation.characterInformation().characterGender());
    newCharacter.setCharacterClass(playerInformation.characterInformation().characterClass());
    newCharacter.setCharacterRace(playerInformation.characterInformation().characterRace());
    newCharacter.setPlayer(player);
    return newCharacter;
  }

  @Override
  public void write(Chunk<? extends ProcessedRaidPGCR> chunk) throws Exception {
    for (ProcessedRaidPGCR pgcr : chunk) {
      for (PlayerInformation pi : pgcr.playerInformation()) {
        Long membershipId = pi.membershipId();
        Player player = playerRepository.findById(membershipId).orElseGet(() -> {
          Player newPlayer = new Player();
          newPlayer.setMembershipId(pi.membershipId());
          newPlayer.setMembershipType(pi.membershipType());
          newPlayer.setDisplayName(pi.displayName());

          // Check for global display name and code in PGCR
          Optional.ofNullable(pi.globalDisplayName())
              .ifPresent(newPlayer::setGlobalDisplayName);
          Optional.ofNullable(pi.globalDisplayNameCode())
              .map(Integer::parseInt)
              .ifPresent(newPlayer::setGlobalDisplayNameCode);

          newPlayer.setPlayerCharacters(new HashSet<>());
          newPlayer.getPlayerCharacters().add(createCharacter(pi, newPlayer));
          return newPlayer;
        });

        if (player.getPlayerCharacters().stream()
            .noneMatch(c -> c.getCharacterId()
                .equals(pi.characterInformation().characterId()))) {
          PlayerCharacter newCharacter = createCharacter(pi, player);
          player.getPlayerCharacters().add(newCharacter);
        }

        // Update if global display name and code are not null
        Optional.ofNullable(pi.globalDisplayName())
            .ifPresent(player::setGlobalDisplayName);
        Optional.ofNullable(pi.globalDisplayNameCode())
            .map(Integer::parseInt)
            .ifPresent(player::setGlobalDisplayNameCode);

        playerRepository.save(player);
      }
    }
  }
}
