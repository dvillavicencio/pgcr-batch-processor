package com.deahstroke.pgcrbatchprocessor.service;

import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import com.deahstroke.pgcrbatchprocessor.enums.Labeled;
import com.deahstroke.pgcrbatchprocessor.exception.ManifestException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ManifestMarshallingService {

  private final RedisTemplate<String, ManifestResponse> redisTemplate;

  public ManifestMarshallingService(RedisTemplate<String, ManifestResponse> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * Return a {@link Labeled} enum based on the manifest entity found
   *
   * @param manifestId   the manifest hash
   * @param labeledClass the labelled enum to fetch
   * @return {@link Labeled} enum
   */
  public <T extends Enum<T> & Labeled> T getLabeled(String manifestId, Class<T> labeledClass) {
    ManifestResponse manifest = getManifest(manifestId).orElseThrow(
        () -> new ManifestException("Manifest not found: " + manifestId)
    );
    return Stream.of(labeledClass.getEnumConstants())
        .filter(e -> e.getLabel().equalsIgnoreCase(manifest.displayProperties().name()))
        .findFirst().orElseThrow(() -> new NoSuchElementException(
            labeledClass.getSimpleName() + " not found: " + manifest.displayProperties().name()));
  }

  /**
   * Fetches an entity from the cache manifest
   *
   * @param manifestId the hash of the manifest entity
   * @return {@link ManifestResponse}
   */
  public Optional<ManifestResponse> getManifest(String manifestId) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(manifestId));
  }
}
