package com.beck.crawler.exception;

public class CrawlTaskInterruptedException extends BaseApplicationException {

  public static final String ERROR_MESSAGE = "Crawl task interrupted";

  public CrawlTaskInterruptedException() {
    super(1003, ERROR_MESSAGE);
  }
}
