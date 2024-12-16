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
public class CompressionProcessor implements ItemProcessor<ProcessedRaidPGCR, RaidPgcr> {

  private final ObjectMapper objectMapper;

  public CompressionProcessor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public RaidPgcr process(ProcessedRaidPGCR item) {
    RaidPgcr raidPgcr = new RaidPgcr();
    raidPgcr.setInstanceId(item.instanceId());
    raidPgcr.setTimestamp(item.startTime());
    raidPgcr.setBlob(serializeAndCompress(item));
    return raidPgcr;
  }

  private byte[] serializeAndCompress(ProcessedRaidPGCR item) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipos = new GZIPOutputStream(baos)) {
      byte[] bytes = objectMapper.writeValueAsBytes(item);
      gzipos.write(bytes);
      gzipos.finish();
      return bytes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
