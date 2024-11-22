package com.deahstroke.pgcrbatchprocessor.controller;

import com.deahstroke.pgcrbatchprocessor.service.AsyncJobRunnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BatchJobController {

  private final AsyncJobRunnerService asyncJobRunnerService;

  public BatchJobController(AsyncJobRunnerService asyncJobRunnerService) {
    this.asyncJobRunnerService = asyncJobRunnerService;
  }

  @PostMapping("/batch/pgcr/run/{jobNumber}")
  public ResponseEntity<Void> handle(@PathVariable Integer jobNumber)
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    log.info("Starting PGCR batch processing with Id [{}]", jobNumber);
    asyncJobRunnerService.runJobAsync(jobNumber);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/batch/pgcr/run/{jobNumber}")
  public ResponseEntity<Void> delete(@PathVariable Integer jobNumber)
      throws NoSuchJobExecutionException, JobExecutionNotRunningException {
    log.info("Stopping PGCR batch job with Id [{}]", jobNumber);
    asyncJobRunnerService.stopJobAsync(jobNumber);
    return ResponseEntity.noContent().build();
  }
}