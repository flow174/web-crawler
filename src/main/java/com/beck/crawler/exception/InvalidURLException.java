package com.beck.crawler.exception;

public class InvalidURLException extends BaseApplicationException {

  public static final String ERROR_MESSAGE = "Invalid URL: %s, make sure follow format: (https|http)://.+";

  public InvalidURLException(String url) {
    super(1005, String.format(ERROR_MESSAGE, url));
  }
}
