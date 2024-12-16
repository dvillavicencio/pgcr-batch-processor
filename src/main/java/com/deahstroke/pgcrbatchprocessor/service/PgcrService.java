package com.deahstroke.pgcrbatchprocessor.service;

import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.repository.RaidPgcrRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.springframework.stereotype.Service;

@Service
public class PgcrService {

  private final RaidPgcrRepository raidPgcrRepository;
  private final ObjectMapper objectMapper;

  public PgcrService(RaidPgcrRepository raidPgcrRepository, ObjectMapper objectMapper) {
    this.raidPgcrRepository = raidPgcrRepository;
    this.objectMapper = objectMapper;
  }

  public ProcessedRaidPGCR getPGCR(Long pgcrId) {
    var pgcr = raidPgcrRepository.getReferenceById(pgcrId);
    try (
        ByteArrayInputStream bais = new ByteArrayInputStream(pgcr.getBlob());
        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
    ) {
      return objectMapper.readValue(gzipInputStream, ProcessedRaidPGCR.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
