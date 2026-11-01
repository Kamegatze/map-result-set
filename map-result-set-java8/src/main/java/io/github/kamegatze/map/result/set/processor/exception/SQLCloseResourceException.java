package io.github.kamegatze.map.result.set.processor.exception;

public class SQLCloseResourceException extends RuntimeException {
  public SQLCloseResourceException(String message) {
    super(message);
  }

  public SQLCloseResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQLCloseResourceException(Throwable cause) {
    super(cause);
  }

  public SQLCloseResourceException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public SQLCloseResourceException() {}
}
