package com.deahstroke.pgcrbatchprocessor.exception;

public class FileProcessingException extends RuntimeException {

  public FileProcessingException(String message) {
    super(message);
  }

  public FileProcessingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
