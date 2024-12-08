package com.deahstroke.pgcrbatchprocessor.entity;

import com.deahstroke.pgcrbatchprocessor.enums.CharacterClass;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterGender;
import com.deahstroke.pgcrbatchprocessor.enums.CharacterRace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "player_character")
public class PlayerCharacter {

  @Id
  @Column(name = "character_id")
  private Long characterId;

  @Enumerated(EnumType.STRING)
  @Column(name = "character_class", columnDefinition = "CHARACTER_CLASS")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private CharacterClass characterClass;

  @Enumerated(EnumType.STRING)
  @Column(name = "character_race", columnDefinition = "CHARACTER_RACE")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private CharacterRace characterRace;

  @Enumerated(EnumType.STRING)
  @Column(name = "character_gender", columnDefinition = "CHARACTER_GENDER")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private CharacterGender characterGender;

  @Column(name = "current_emblem")
  private String currentEmblem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_membership_id", nullable = false)
  private Player player;
}
