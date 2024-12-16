package com.deahstroke.pgcrbatchprocessor.job;

import com.deahstroke.pgcrbatchprocessor.dto.ProcessedRaidPGCR;
import com.deahstroke.pgcrbatchprocessor.entity.RaidPgcr;
import com.deahstroke.pgcrbatchprocessor.facade.PlayerAggregateStatsWriter;
import com.deahstroke.pgcrbatchprocessor.facade.PlayerStatsWriter;
import com.deahstroke.pgcrbatchprocessor.facade.PlayerWriter;
import com.deahstroke.pgcrbatchprocessor.facade.RaidWriter;
import jakarta.persistence.EntityManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class PgcrDatabaseRelayStep {

  @Bean
  Step overallProcessingStep(JobRepository jobRepository,
      JpaTransactionManager jpaTransactionManager,
      JpaPagingItemReader<RaidPgcr> jpaPagingItemReader,
      ItemProcessor<RaidPgcr, ProcessedRaidPGCR> raidPgcrProcessor,
      ItemWriter<ProcessedRaidPGCR> raidPgcrWriter) {
    return new StepBuilder("raidPgcrProcessStep", jobRepository)
        .<RaidPgcr, ProcessedRaidPGCR>chunk(500, jpaTransactionManager)
        .reader(jpaPagingItemReader)
        .processor(raidPgcrProcessor)
        .writer(raidPgcrWriter)
        .build();
  }

  @Bean
  public JpaPagingItemReader<RaidPgcr> raidPgcrReader(EntityManagerFactory entityManagerFactory) {
    return new JpaPagingItemReaderBuilder<RaidPgcr>()
        .name("raidPgcrReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(500)
        .queryString("SELECT * FROM raid_pgcr")
        .build();
  }

  @Bean
  public ItemProcessor<RaidPgcr, ProcessedRaidPGCR> raidPgcrProcessor() {
    return item -> {
      try (
          ByteArrayInputStream bais = new ByteArrayInputStream(item.getBlob());
          GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
          ObjectInputStream ois = new ObjectInputStream(gzipInputStream)
      ) {
        return (ProcessedRaidPGCR) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Bean
  public ItemWriter<ProcessedRaidPGCR> raidPgcrWriter(PlayerWriter playerWriter,
      RaidWriter raidWriter, PlayerStatsWriter playerStatsWriter,
      PlayerAggregateStatsWriter playerAggregateStatsWriter) {
    CompositeItemWriter<ProcessedRaidPGCR> itemWriter = new CompositeItemWriter<>();
    List<ItemWriter<? super ProcessedRaidPGCR>> itemWriters = new ArrayList<>();
    itemWriters.add(raidWriter);
    itemWriters.add(playerWriter);
    itemWriters.add(playerStatsWriter);
    itemWriters.add(playerAggregateStatsWriter);

    itemWriter.setDelegates(itemWriters);
    return itemWriter;
  }

}
