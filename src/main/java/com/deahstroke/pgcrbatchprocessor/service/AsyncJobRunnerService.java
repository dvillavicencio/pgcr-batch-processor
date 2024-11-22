package com.deahstroke.pgcrbatchprocessor.service;

import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncJobRunnerService {

  private final JobLauncher jobLauncher;
  private final JobOperator jobOperator;
  private final Job pgcrJob;

  public AsyncJobRunnerService(JobLauncher jobLauncher, JobOperator jobOperator, Job pgcrJob) {
    this.jobLauncher = jobLauncher;
    this.jobOperator = jobOperator;
    this.pgcrJob = pgcrJob;
  }

  @Async
  public void runJobAsync(Integer jobNumber)
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    jobLauncher.run(pgcrJob,
        new JobParameters(Map.of("jobNumber", new JobParameter<>(jobNumber, Integer.class))));
  }

  @Async
  public void stopJobAsync(Integer jobNumber)
      throws NoSuchJobExecutionException, JobExecutionNotRunningException {
    jobOperator.stop(jobNumber);
  }
}
