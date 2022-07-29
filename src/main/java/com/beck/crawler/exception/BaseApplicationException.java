package com.beck.crawler.exception;

import lombok.Getter;

@Getter
public class BaseApplicationException extends RuntimeException {

  private static final long serialVersionUID = -6241034866000020448L;

  private final int errorCode;

  private final String errorMessage;

  public BaseApplicationException(int errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

}
