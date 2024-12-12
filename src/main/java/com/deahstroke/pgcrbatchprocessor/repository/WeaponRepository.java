package com.deahstroke.pgcrbatchprocessor.repository;

import com.deahstroke.pgcrbatchprocessor.entity.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {

}
