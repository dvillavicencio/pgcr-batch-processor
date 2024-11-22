package com.deahstroke.pgcrbatchprocessor.configuration;

import com.deahstroke.pgcrbatchprocessor.client.BungieClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Data
@Configuration
@ConfigurationProperties(prefix = "bungie.api")
public class BungieConfiguration {

  /**
   * The name of the Bungie API key header
   */
  private static final String API_KEY_HEADER_NAME = "x-api-key";

  /**
   * API key provided by Bungie when registering an application in their portal
   */
  private String key;

  /**
   * Base url for Bungie Requests
   */
  private String baseUrl;

  /**
   * Default bungie client used to make general API calls to Bungie.net
   *
   * @param builder The default WebClient.Builder defined in the main application
   * @return {@link BungieClient}
   */
  @Bean("defaultBungieClient")
  public BungieClient defaultBungieClient(RestClient.Builder builder) {
    var restClient = builder
        .baseUrl(this.baseUrl)
        .defaultHeader(API_KEY_HEADER_NAME, this.key)
        .build();
    return HttpServiceProxyFactory.builder()
        .exchangeAdapter(RestClientAdapter.create(restClient))
        .build()
        .createClient(BungieClient.class);
  }
}
