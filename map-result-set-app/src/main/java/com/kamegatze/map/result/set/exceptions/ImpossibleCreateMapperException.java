package com.kamegatze.map.result.set.exceptions;

public class ImpossibleCreateMapperException extends RuntimeException {
  public ImpossibleCreateMapperException(String message) {
    super(message);
  }

  public ImpossibleCreateMapperException(String message, Throwable cause) {
    super(message, cause);
  }

  public ImpossibleCreateMapperException(Throwable cause) {
    super(cause);
  }
}
