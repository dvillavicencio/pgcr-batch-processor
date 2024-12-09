package com.deahstroke.pgcrbatchprocessor.writer;

import com.deahstroke.pgcrbatchprocessor.entity.Player;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class PlayerFlatMapWriter implements ItemWriter<List<com.deahstroke.pgcrbatchprocessor.dto.PlayerDto>> {

  private static final ConcurrentHashMap<Long, Player> records = new ConcurrentHashMap<>();

  @Override
  public void write(Chunk<? extends List<com.deahstroke.pgcrbatchprocessor.dto.PlayerDto>> chunk) throws Exception {

  }
}
