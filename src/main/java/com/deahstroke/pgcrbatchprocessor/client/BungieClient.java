package com.deahstroke.pgcrbatchprocessor.client;

import com.deahstroke.pgcrbatchprocessor.dto.BungieResponse;
import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface BungieClient {

  @GetExchange("/Destiny2/Manifest/{entityType}/{hashIdentifier}")
  ResponseEntity<BungieResponse<ManifestResponse>> getManifestEntity(
      @PathVariable String entityType, @PathVariable Long hashIdentifier);
}
