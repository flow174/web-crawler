package com.beck.crawler.exception;

public class NoSuchQueryIdException extends BaseApplicationException{

  public static final String ERROR_MESSAGE = "No such query id";

  public NoSuchQueryIdException() {
    super(1002, ERROR_MESSAGE);
  }
}
