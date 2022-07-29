package com.beck.crawler.utils;

import com.beck.crawler.config.CrawlerConfig;

public class CrawlUtils {

  private final static String invalidRegex = "[#/*].+";
  private final static String validRegex = "(https|http)://.+";
  private static CrawlUtils instance;

  private CrawlUtils() {
  }

  public static CrawlUtils getInstance() {
    return (instance != null) ? instance : new CrawlUtils();
  }

  public boolean isNotValidLink(String link) {
    return link.isEmpty() || link.matches(invalidRegex);
  }

  public boolean isValidLink(String link) {
    return !link.isEmpty() && link.matches(validRegex);
  }

}
