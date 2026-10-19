package io.github.kamegatze.map.result.set.processor.exception;

public class ExtractResultSetException extends RuntimeException {
  public ExtractResultSetException(String message) {
    super(message);
  }

  public ExtractResultSetException(String message, Throwable cause) {
    super(message, cause);
  }
}
