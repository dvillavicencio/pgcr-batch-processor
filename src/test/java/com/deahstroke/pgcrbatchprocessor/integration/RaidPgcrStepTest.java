package com.deahstroke.pgcrbatchprocessor.integration;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;

class RaidPgcrStepTest extends BaseIntegrationTest {

  @Test
  void raidPgcrStepShouldBeSuccessful() throws Exception {
    JobExecution jobExecution = this.jobLauncherTestUtils.launchStep("workerStep");
    var stepExecutions = jobExecution.getStepExecutions();
    var actualJobExitStatus = jobExecution.getExitStatus();
  }
}
