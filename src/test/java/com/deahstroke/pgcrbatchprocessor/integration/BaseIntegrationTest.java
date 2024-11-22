package com.deahstroke.pgcrbatchprocessor.integration;

import com.deahstroke.pgcrbatchprocessor.job.JobRunner;
import org.junit.After;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {JobRunner.class})
@Testcontainers
@ContextConfiguration(classes = JobRunner.class)
@AutoConfigureWireMock(files = "/build/resources/test/__files", port = 0)
class BaseIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:17.0"))
      .withDatabaseName("rivenbot")
      .withUsername("root")
      .withPassword("root");

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  JobRepositoryTestUtils jobRepositoryTestUtils;

  @DynamicPropertySource
  static void propertySource(DynamicPropertyRegistry registry) {
    int wiremockPort = WireMockSpring.options().portNumber();
    registry.add("spring.datasource.url",
        () -> "jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort()
            + "/rivenbot");
    registry.add("spring.datasource.username", () -> "root");
    registry.add("spring.datasource.password", () -> "root");
    registry.add("bungie.api.baseUrl", () -> "http://localhost:" + wiremockPort);
  }

  @After
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

}
