package com.deahstroke.pgcrbatchprocessor.processor;

import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.RaidPgcr;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class RaidPgcrItemProcessor implements ItemProcessor<ProcessedRaidPGCR, RaidPgcr> {

  private final ObjectMapper objectMapper;

  public RaidPgcrItemProcessor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public RaidPgcr process(ProcessedRaidPGCR item) {
    RaidPgcr raidPgcr = new RaidPgcr();
    raidPgcr.setInstanceId(item.instanceId());
    raidPgcr.setTimestamp(item.startTime());

    // Compress using Gzip
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOs = new GZIPOutputStream(baos)) {
      objectMapper.writeValue(gzipOs, item);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    raidPgcr.setBlob(baos.toByteArray());
    return raidPgcr;
  }
}
