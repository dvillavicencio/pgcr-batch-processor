package com.deahstroke.pgcrbatchprocessor.exception;

public class NoSuchPlayerException extends RuntimeException {

  public NoSuchPlayerException(String formatted) {
    super(formatted);
  }
}
