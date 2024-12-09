package com.deahstroke.pgcrbatchprocessor.job;

import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.entity.Player;
import com.deahstroke.pgcrbatchprocessor.entity.PlayerCharacter;
import com.deahstroke.pgcrbatchprocessor.processor.PlayerProcessor;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerCharacterRepository;
import com.deahstroke.pgcrbatchprocessor.repository.PlayerRepository;
import com.deahstroke.pgcrbatchprocessor.utils.CustomBufferedReaderFactory;
import com.deahstroke.pgcrbatchprocessor.utils.ZstdJsonItemReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

@Configuration
public class PlayerPgcrStep {

  private static final Logger log = LoggerFactory.getLogger(PlayerPgcrStep.class);

  private static final String RESOURCE_DIRECTORY = "/Volumes/T7 Shield/Bungo Stuff/bungo-pgcr/bungo-pgcr/";
  private static final Integer DEFAULT_CHUNK_SIZE = 100;

  @Bean
  public Job playerJob(
      JobRepository jobRepository,
      Step playerManagerStep) {
    return new JobBuilder("playerProcessingJob", jobRepository)
        .start(playerManagerStep)
        .build();
  }

  @Bean
  public Step playerManagerStep(
      JobRepository jobRepository,
      PartitionHandler playerPartitionHandler,
      Partitioner partitioner) {
    return new StepBuilder("playerStepManager", jobRepository)
        .partitioner("playerWorkerStep", partitioner)
        .partitionHandler(playerPartitionHandler)
        .build();
  }

  @Bean
  public Step playerWorkerStep(JobRepository jobRepository,
      JpaTransactionManager jpaTransactionManager,
      FlatFileItemReader<PostGameCarnageReport> playerPgcrItemReader,
      PlayerProcessor playerProcessor,
      ItemListWriter itemListWriter) {
    return new StepBuilder("playerWorkerStep", jobRepository)
        .<PostGameCarnageReport, List<Player>>chunk(DEFAULT_CHUNK_SIZE, jpaTransactionManager)
        .reader(playerPgcrItemReader)
        .processor(playerProcessor)
        .writer(itemListWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<PostGameCarnageReport> playerPgcrItemReader(
      @Value("#{stepExecutionContext[filename]}") String filename, ObjectMapper objectMapper) {
    ZstdJsonItemReader itemReader = new ZstdJsonItemReader();
    itemReader.setLineMapper(
        ((line, lineNumber) -> objectMapper.readValue(line, PostGameCarnageReport.class)));
    itemReader.setBufferedReaderFactory(new CustomBufferedReaderFactory());
    itemReader.setResource(new FileSystemResource(RESOURCE_DIRECTORY + filename));
    itemReader.setEncoding("UTF-8");
    itemReader.setSaveState(false);
    return itemReader;
  }

  @Bean
  public PartitionHandler playerPartitionHandler(
      Step playerWorkerStep,
      TaskExecutor masterThreadExecutor) {
    TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
    handler.setStep(playerWorkerStep);
    handler.setGridSize(20);
    handler.setTaskExecutor(masterThreadExecutor);
    return handler;
  }

  @Component
  @StepScope
  public static class ItemListWriter implements ItemWriter<List<Player>> {

    private final PlayerRepository playerRepository;
    private final PlayerCharacterRepository playerCharacterRepository;

    public ItemListWriter(PlayerRepository playerRepository,
        PlayerCharacterRepository playerCharacterRepository) {
      this.playerRepository = playerRepository;
      this.playerCharacterRepository = playerCharacterRepository;
    }

    @Override
    public void write(Chunk<? extends List<Player>> chunk) {
      for (List<Player> chunks : chunk) {
        for (Player player : chunks) {
          playerRepository.insertOnConflict(player);
          for (PlayerCharacter character : player.getPlayerCharacters()) {
            playerCharacterRepository.insertOnConflict(character);
          }
        }
      }
    }
  }

}
