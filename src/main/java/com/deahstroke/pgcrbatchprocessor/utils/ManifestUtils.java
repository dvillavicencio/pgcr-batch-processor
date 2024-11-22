package com.deahstroke.pgcrbatchprocessor.utils;

import com.deahstroke.pgcrbatchprocessor.client.CachedBungieClient;
import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManifestUtils {

  private ManifestUtils() {

  }

  /**
   * Retrievs a manifest entity using a client using caching strategy to call Bungie's API
   *
   * @param client     the cached Bungie client
   * @param entityType the type of the entity to fetch from the manifest
   * @param hash       the hash of the entity to fetch from the manifest
   * @param id         the ID of the PGCR, this is only for logging puposes
   * @return {@link ManifestResponse}
   */
  public static ManifestResponse manifestEntity(CachedBungieClient client, String entityType,
      Long hash, Long id) {
    var response = client.getManifestEntity(entityType, hash);
    if (response.getStatusCode().isError() || response.getBody() == null
        || response.getBody().response() == null) {
      log.warn(
          "Something went wrong with the Destiny 2 Manifest: [{}}], [{}}]. This warning came from PGCR with ID [{}]",
          entityType, hash, id);
      return null;
    }
    if (response.getBody().response() == null) {
      return null;
    }
    return response.getBody().response();
  }
}
