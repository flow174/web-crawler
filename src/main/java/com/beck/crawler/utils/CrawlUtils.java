package com.beck.crawler.utils;

public class CrawlUtils {

  private final static String validRegex = "(https|http)://.+";

  private static CrawlUtils instance;

  private CrawlUtils() {
  }

  public static CrawlUtils getInstance() {
    return (instance != null) ? instance : new CrawlUtils();
  }

  public boolean isValidLink(String link) {
    return !link.isEmpty() && link.matches(validRegex);
  }

}
