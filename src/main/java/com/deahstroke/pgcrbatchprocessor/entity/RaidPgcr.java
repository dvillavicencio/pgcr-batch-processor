package com.deahstroke.pgcrbatchprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "raid_pgcr")
public class RaidPgcr {

  @Id
  @Column(name = "instance_id")
  private Long instanceId;

  @Column(name = "timestamp")
  private Instant timestamp;

  @Column(name = "blob")
  private byte[] blob;
}
