package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

  @Modifying
  @Query(value =
      "INSERT INTO player (membership_id, membership_type, global_display_name, global_display_name_code, display_name) "
          + "VALUES (:#{#player.membershipId}, :#{#player.membershipType}, :#{#player.globalDisplayName},"
          + ":#{#player.globalDisplayNameCode}, :#{#player.displayName}) "
          + "ON CONFLICT(membership_id) DO UPDATE SET "
          + "global_display_name = EXCLUDED.global_display_name, "
          + "global_display_name_code = EXCLUDED.global_display_name_code",
      nativeQuery = true)
  void insertOnConflict(Player player);
}
