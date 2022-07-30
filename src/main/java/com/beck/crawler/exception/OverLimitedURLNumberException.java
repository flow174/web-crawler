package com.beck.crawler.exception;

public class OverLimitedURLNumberException extends BaseApplicationException {

  public static final String ERROR_MESSAGE = "Over limited URL number: %d";

  public OverLimitedURLNumberException(int num) {
    super(1001, String.format(ERROR_MESSAGE, num));
  }
}
