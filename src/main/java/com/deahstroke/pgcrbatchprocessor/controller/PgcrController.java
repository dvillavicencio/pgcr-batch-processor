package com.deahstroke.pgcrbatchprocessor.controller;

import com.deahstroke.pgcrbatchprocessor.service.PgcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PgcrController {

  private final PgcrService pgcrService;

  public PgcrController(PgcrService pgcrService) {
    this.pgcrService = pgcrService;
  }

  @GetMapping("/{pgcrId}")
  public ResponseEntity<String> getPGCR(@PathVariable Long pgcrId) {
    return ResponseEntity.ok(pgcrService.getPGCR(pgcrId));
  }

}
