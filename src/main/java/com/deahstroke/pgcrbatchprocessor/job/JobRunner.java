package com.deahstroke.pgcrbatchprocessor.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunner {

  private final Step pgcrProcessingStep;

  public JobRunner(
      Step pgcrProcessingStep) {
    this.pgcrProcessingStep = pgcrProcessingStep;
  }

  @Bean
  public Job pgcrJob(JobRepository jobRepository) {
    return new JobBuilder("pgcrPartitioningProcessingJob", jobRepository)
        .start(pgcrProcessingStep)
        .build();
  }
}
