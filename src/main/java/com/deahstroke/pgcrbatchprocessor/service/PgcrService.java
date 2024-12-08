package com.deahstroke.pgcrbatchprocessor.service;

import com.deahstroke.pgcrbatchprocessor.repository.RaidPgcrRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import org.springframework.stereotype.Service;

@Service
public class PgcrService {

  private final RaidPgcrRepository raidPgcrRepository;

  public PgcrService(RaidPgcrRepository raidPgcrRepository) {
    this.raidPgcrRepository = raidPgcrRepository;
  }

  public Object getPGCR(Long pgcrId) {
    var pgcr = raidPgcrRepository.getReferenceById(pgcrId);
    try (
        ByteArrayInputStream bais = new ByteArrayInputStream(pgcr.getBlob());
        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        ObjectInputStream ois = new ObjectInputStream(gzipInputStream)
    ) {
      return ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
