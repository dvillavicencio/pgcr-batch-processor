package com.deahstroke.pgcrbatchprocessor.client;

import com.deahstroke.pgcrbatchprocessor.dto.BungieResponse;
import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CachedBungieClient {

  private final BungieClient bungieClient;

  public CachedBungieClient(BungieClient bungieClient) {
    this.bungieClient = bungieClient;
  }

  @Cacheable(cacheManager = "inMemoryCacheManager", cacheNames = "manifest")
  public ResponseEntity<BungieResponse<ManifestResponse>> getManifestEntity(
      String entityType, Long hash) {
    return bungieClient.getManifestEntity(entityType, hash);
  }
}
