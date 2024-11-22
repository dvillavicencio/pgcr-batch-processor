package com.deahstroke.pgcrbatchprocessor.service;

import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.repository.RaidPgcrRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import org.springframework.stereotype.Service;

@Service
public class PgcrService {

  private final RaidPgcrRepository raidPgcrRepository;

  public PgcrService(RaidPgcrRepository raidPgcrRepository) {
    this.raidPgcrRepository = raidPgcrRepository;
  }

  public ProcessedRaidPGCR getPGCR(Long pgcrId) {
    var pgcr = raidPgcrRepository.getReferenceById(pgcrId);
    try (
        ByteArrayInputStream bais = new ByteArrayInputStream(pgcr.getBlob());
        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)
    ) {
      return (ProcessedRaidPGCR) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
