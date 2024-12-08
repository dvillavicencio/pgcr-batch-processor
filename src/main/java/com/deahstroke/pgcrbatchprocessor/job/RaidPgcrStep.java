package com.deahstroke.pgcrbatchprocessor.job;

import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.entity.RaidPgcr;
import com.deahstroke.pgcrbatchprocessor.exception.LineTooLargeException;
import com.deahstroke.pgcrbatchprocessor.processor.PgcrProcessor;
import com.deahstroke.pgcrbatchprocessor.processor.RaidPgcrItemProcessor;
import com.deahstroke.pgcrbatchprocessor.repository.RaidPgcrRepository;
import com.deahstroke.pgcrbatchprocessor.utils.CustomBufferedReaderFactory;
import com.deahstroke.pgcrbatchprocessor.utils.CustomMultiResourcePartitioner;
import com.deahstroke.pgcrbatchprocessor.utils.ZstdJsonItemReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RaidPgcrStep {

  private static final Logger log = LoggerFactory.getLogger(RaidPgcrStep.class);

  private static final String RESOURCE_DIRECTORY = "/Volumes/T7 Shield/Bungo Stuff/bungo-pgcr/bungo-pgcr/";
  private static final Integer MAX_SIZE_FOR_PGCR = 128 * 1024 * 1024;
  private static final Integer DEFAULT_CHUNK_SIZE = 6000;

  private StepExecutionListener afterPgcrStepListener() {
    return new StepExecutionListener() {
      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        String filename = stepExecution.getExecutionContext().getString("filename");
        if (stepExecution.getStatus().equals(BatchStatus.FAILED)) {
          log.error("Step with context [{}] exited abnormally for file with name [{}]",
              stepExecution, filename);
          return ExitStatus.FAILED;
        } else {
          log.info(
              "Step with context [{}] finished. Marking file [{}] for deletion",
              stepExecution.getStepName(), filename);
          try {
            Files.move(Path.of(RESOURCE_DIRECTORY + filename),
                Path.of(RESOURCE_DIRECTORY + filename + ".del"));
          } catch (IOException e) {
            log.error("Error marking file [{}] for deletion", filename, e);
            return ExitStatus.FAILED;
          }
        }
        return StepExecutionListener.super.afterStep(stepExecution);
      }
    };
  }

  @Bean
  public StepExecutionListener statusStepListener(MeterRegistry meterRegistry) {
    return new StepExecutionListener() {

      @Override
      public void beforeStep(StepExecution stepExecution) {
        meterRegistry.counter("spring_batch_step_status",
                Tags.of("step", stepExecution.getStepName(), "status", "STARTED"))
            .increment();
        StepExecutionListener.super.beforeStep(stepExecution);
      }

      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Worker step [{}}] is processing file [{}]", stepExecution.getStepName(),
            stepExecution.getExecutionContext().getString("filename"));
        String status = stepExecution.getExitStatus().getExitCode();
        meterRegistry.counter("spring_batch_step_status",
                Tags.of("step", stepExecution.getStepName(), "status", status))
            .increment();
        return StepExecutionListener.super.afterStep(stepExecution);
      }
    };
  }

  @Bean
  public TaskExecutor masterThreadExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(15);
    executor.setMaxPoolSize(20);
    executor.setThreadNamePrefix("Partitioned-Step-");
    executor.initialize();
    return executor;
  }

  @Bean
  public Job pgcrJob(JobRepository jobRepository,
      Step pgcrProcessingStep) {
    return new JobBuilder("pgcrPartitioningProcessingJob", jobRepository)
        .start(pgcrProcessingStep)
        .build();
  }

  @Bean
  public Step pgcrProcessingStep(
      JobRepository jobRepository,
      PartitionHandler partitionHandler) {
    return new StepBuilder("partitionedStep", jobRepository)
        .partitioner("workerStep", partitioner())
        .partitionHandler(partitionHandler)
        .build();
  }

  @Bean
  public PartitionHandler partitionHandler(
      Step pgcrWorkerStep,
      TaskExecutor masterThreadExecutor) {
    TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
    handler.setStep(pgcrWorkerStep);
    handler.setGridSize(20);
    handler.setTaskExecutor(masterThreadExecutor);
    return handler;
  }

  @Bean
  public Step pgcrWorkerStep(
      JobRepository jobRepository,
      JpaTransactionManager jpaTransactionManager,
      FlatFileItemReader<PostGameCarnageReport> raidPgcrItemReader,
      CompositeItemProcessor<PostGameCarnageReport, RaidPgcr> pgcrCompositeProcessor,
      RepositoryItemWriter<RaidPgcr> raidPgcrRepositoryItemWriter,
      StepExecutionListener statusStepListener,
      MeterRegistry meterRegistry) {
    return new StepBuilder("worker-step", jobRepository)
        .<PostGameCarnageReport, RaidPgcr>chunk(DEFAULT_CHUNK_SIZE, jpaTransactionManager)
        .reader(raidPgcrItemReader)
        .processor(pgcrCompositeProcessor)
        .writer(raidPgcrRepositoryItemWriter)
        .listener(afterPgcrStepListener())
        .listener(statusStepListener)
        .faultTolerant()
        .skipPolicy((t, skipCount) -> {
          if (t instanceof LineTooLargeException || t instanceof FlatFileParseException) {
            skipCount++;
            return true;
          }
          return false;
        })
        .meterRegistry(meterRegistry)
        .build();
  }

  @Bean
  public Partitioner partitioner() {
    CustomMultiResourcePartitioner partitioner
        = new CustomMultiResourcePartitioner();
    Resource[] resources;
    try {
      ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
      resources = resourcePatternResolver
          .getResources("file:" + RESOURCE_DIRECTORY + "*.zst");
    } catch (IOException e) {
      throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
    }
    partitioner.setResources(resources);
    return partitioner;
  }

  @Bean
  public CompositeItemProcessor<PostGameCarnageReport, RaidPgcr> pgcrCompositeProcessor(
      PgcrProcessor pgcrProcessor,
      RaidPgcrItemProcessor raidPgcrItemProcessor) {
    return new CompositeItemProcessor<>(
        pgcrProcessor, raidPgcrItemProcessor);
  }

  @Bean
  @StepScope
  public FlatFileItemReader<PostGameCarnageReport> raidPgcrItemReader(
      @Value("#{stepExecutionContext[filename]}") String filename, ObjectMapper objectMapper) {
    ZstdJsonItemReader itemReader = new ZstdJsonItemReader();
    itemReader.setLineMapper(((line, lineNumber) -> {
      if (line.getBytes(StandardCharsets.UTF_8).length > MAX_SIZE_FOR_PGCR) {
        String pgcrNumber = filename.split("-")[0] + lineNumber;
        log.warn("Line is larger than 128KB for PGCR [%s]".formatted(pgcrNumber));
        throw new LineTooLargeException(
            "Line is larger than 128KB for PGCR [%s]".formatted(pgcrNumber));
      }
      return objectMapper.readValue(line, PostGameCarnageReport.class);
    }));
    itemReader.setBufferedReaderFactory(new CustomBufferedReaderFactory());
    itemReader.setResource(new FileSystemResource(RESOURCE_DIRECTORY + filename));
    itemReader.setEncoding("UTF-8");
    itemReader.setSaveState(false);
    return itemReader;
  }

  @Bean
  public RepositoryItemWriter<RaidPgcr> raidPgcrItemWriter(RaidPgcrRepository raidPgcrRepository) {
    RepositoryItemWriter<RaidPgcr> itemWriter = new RepositoryItemWriter<>();
    itemWriter.setRepository(raidPgcrRepository);
    return itemWriter;
  }

}
