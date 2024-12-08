package com.deahstroke.pgcrbatchprocessor;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PgcrBatchProcessorApplication {

  public static void main(String[] args) {
    SpringApplication.run(PgcrBatchProcessorApplication.class, args);
  }

  @Bean
  public CacheManager inMemoryCacheManager() {
    return new ConcurrentMapCacheManager();
  }

  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
