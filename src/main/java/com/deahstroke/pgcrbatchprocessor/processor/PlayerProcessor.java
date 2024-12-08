package com.deahstroke.pgcrbatchprocessor.processor;

import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReportEntry;
import com.deahstroke.pgcrbatchprocessor.entity.Player;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerCharacter;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRepository;
import com.deahstroke.pgcrbatchprocessor.utils.EnumUtils;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PlayerProcessor implements ItemProcessor<PostGameCarnageReport, List<Player>> {

  private final PlayerRepository playerRepository;
  private final RedisTemplate<String, ManifestResponse> redisTemplate;

  public PlayerProcessor(PlayerRepository playerRepository,
      RedisTemplate<String, ManifestResponse> redisTemplate) {
    this.playerRepository = playerRepository;
    this.redisTemplate = redisTemplate;
  }

  @Override
  public List<Player> process(PostGameCarnageReport item) throws Exception {
    // Skip on empty list or empty membership type
    if (CollectionUtils.isEmpty(item.entries())
        || item.entries().getFirst().player().destinyUserInfo().membershipType() == 0) {
      return null;
    }
    return item.entries().stream()
        .sorted(Comparator.comparing(entry -> entry.player().destinyUserInfo().membershipType()))
        .map(this::processPGCR)
        .toList();
  }

  private PlayerCharacter createCharacter(PostGameCarnageReportEntry entry,
      Player player) {
    PlayerCharacter newCharacter = new PlayerCharacter();
    newCharacter.setCharacterId(entry.characterId());

    // Get hashes saved from Redis
    var manifestClass = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().classHash()));
    var manifestGender = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().genderHash()));
    var manifestRace = redisTemplate.opsForValue()
        .get(String.valueOf(entry.player().raceHash()));

    CharacterClass characterClazz = EnumUtils.getByLabel(CharacterClass.class,
        manifestClass == null || manifestClass.displayProperties() == null ? "Empty"
            : manifestClass.displayProperties().name());
    CharacterGender characterGender = EnumUtils.getByLabel(CharacterGender.class,
        manifestGender == null || manifestGender.displayProperties() == null ? "Empty" :
            manifestGender.displayProperties().name());
    CharacterRace characterRace = EnumUtils.getByLabel(CharacterRace.class,
        manifestRace == null || manifestRace.displayProperties() == null ? "Empty" :
            manifestRace.displayProperties().name());

    newCharacter.setCharacterGender(characterGender);
    newCharacter.setCharacterClass(characterClazz);
    newCharacter.setCharacterRace(characterRace);
    newCharacter.setPlayer(player);
    return newCharacter;
  }

  private Player processPGCR(PostGameCarnageReportEntry entry) {
    Long membershipId = entry.player().destinyUserInfo().membershipId();
    Player player = playerRepository.findById(membershipId).orElseGet(() -> {
      Player newPlayer = new Player();
      newPlayer.setMembershipId(entry.player().destinyUserInfo().membershipId());
      newPlayer.setMembershipType(entry.player().destinyUserInfo().membershipType());
      newPlayer.setDisplayName(entry.player().destinyUserInfo().displayName());

      // Check for global display name and code in PGCR
      Optional.ofNullable(entry.player().destinyUserInfo().bungieGlobalDisplayName())
          .ifPresent(newPlayer::setGlobalDisplayName);
      Optional.ofNullable(entry.player().destinyUserInfo().bungieGlobalDisplayNameCode())
          .map(Integer::parseInt)
          .ifPresent(newPlayer::setGlobalDisplayNameCode);

      newPlayer.setPlayerCharacters(new HashSet<>());
      newPlayer.getPlayerCharacters().add(createCharacter(entry, newPlayer));
      return newPlayer;
    });

    if (player.getPlayerCharacters().stream()
        .noneMatch(c -> c.getCharacterId().equals(entry.characterId()))) {
      PlayerCharacter newCharacter = createCharacter(entry, player);
      player.getPlayerCharacters().add(newCharacter);
    }

    // Update if global display name and code are not null
    Optional.ofNullable(entry.player().destinyUserInfo().bungieGlobalDisplayName())
        .ifPresent(player::setGlobalDisplayName);
    Optional.ofNullable(entry.player().destinyUserInfo().bungieGlobalDisplayNameCode())
        .map(Integer::parseInt)
        .ifPresent(player::setGlobalDisplayNameCode);

    return player;
  }
}
