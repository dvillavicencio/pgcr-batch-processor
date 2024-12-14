package com.deahstroke.pgcrbatchprocessor.entity;

import com.deahstroke.pgcrbatchprocessor.enums.RaidDifficulty;
import com.deahstroke.pgcrbatchprocessor.enums.RaidName;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RaidId {

  @Column(name = "raid_name", columnDefinition = "raid_name")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private RaidName raidName;

  @Column(name = "raid_difficulty", columnDefinition = "raid_difficulty")
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private RaidDifficulty raidDifficulty;

}
