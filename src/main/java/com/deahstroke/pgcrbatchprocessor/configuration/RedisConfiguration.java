package com.deahstroke.pgcrbatchprocessor.configuration;

import com.deahstroke.pgcrbatchprocessor.dto.ManifestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  @Bean
  RedisTemplate<String, ManifestResponse> redisTemplate(
      RedisConnectionFactory redisConnectionFactory,
      ObjectMapper objectMapper) {
    RedisTemplate<String, ManifestResponse> template = new RedisTemplate<>();
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(
        new Jackson2JsonRedisSerializer<>(objectMapper, ManifestResponse.class));
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }
}
