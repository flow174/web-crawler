package com.beck.crawler.exception;

import java.rmi.UnexpectedException;
import javax.validation.UnexpectedTypeException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class WebCrawlerExceptionHandler {

  @Data
  @Builder
  public static class ErrorBody {

    private int errorCode;
    private String message;
  }

  @ExceptionHandler(BaseApplicationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  ErrorBody handlerException(BaseApplicationException e) {
    return ErrorBody.builder()
        .errorCode(e.getErrorCode())
        .message(e.getErrorMessage())
        .build();
  }

  @ExceptionHandler({InterruptedException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  ErrorBody handlerInterruptedException(InterruptedException ex) {
    log.error("Internal server error:", ex);
    if (log.isDebugEnabled()) {
      return ErrorBody.builder().message(ex.getMessage()).build();
    } else {
      return ErrorBody.builder().message("Internal server error.").build();
    }
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ErrorBody handlerArgumentNotValidException(MethodArgumentNotValidException e) {
    return ErrorBody.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler({UnexpectedException.class, UnexpectedTypeException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ErrorBody handlerUnexpectedTypeException(UnexpectedTypeException e) {
    return ErrorBody.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler({Throwable.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  ErrorBody handlerException(Throwable t) {
    log.error("Internal server error:", t);
    if (log.isDebugEnabled()) {
      return ErrorBody.builder().message(t.getMessage()).build();
    } else {
      return ErrorBody.builder().message("Internal server error.").build();
    }
  }
}
