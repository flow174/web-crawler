package com.beck.crawler.exception;

public class ReadWebsiteException extends BaseApplicationException {

  public static final String ERROR_MESSAGE = "Cannot access the web page %s";

  public ReadWebsiteException(String url) {
    super(1001, String.format(ERROR_MESSAGE, url));
  }
}
