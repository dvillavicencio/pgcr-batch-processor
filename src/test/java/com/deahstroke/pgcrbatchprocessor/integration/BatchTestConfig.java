package com.deahstroke.pgcrbatchprocessor.integration;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchTestConfig {

  @Bean
  public JobLauncherTestUtils jobLauncherTestUtils() {
    return new JobLauncherTestUtils();
  }

  @Bean
  public JobRepositoryTestUtils jobRepositoryTestUtils() {
    return new JobRepositoryTestUtils();
  }
}
