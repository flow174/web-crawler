package com.beck.crawler.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "config.crawl")
public class CrawlerConfig {

  private int maxUrlNum;

  private int deepLevel;

  private int timeOutSecond;

}
