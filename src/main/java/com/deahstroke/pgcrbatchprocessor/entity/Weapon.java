package com.deahstroke.pgcrbatchprocessor.entity;

import com.deahstroke.pgcrbatchprocessor.enums.EquipmentSlot;
import com.deahstroke.pgcrbatchprocessor.enums.WeaponDamageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "weapon")
public class Weapon {

  @Id
  @Column(name = "weapon_hash")
  private Long weaponHash;

  @Column(name = "weapon_icon")
  private String weaponIcon;

  @Column(name = "weapon_damage_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private WeaponDamageType weaponDamageType;

  @Column(name = "weapon_equipment_slot")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private EquipmentSlot equipmentSlot;
}
