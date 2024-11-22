package com.deahstroke.pgcrbatchprocessor.exception;

public class ObjectToByteException extends RuntimeException {

  private String message;

  public ObjectToByteException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
