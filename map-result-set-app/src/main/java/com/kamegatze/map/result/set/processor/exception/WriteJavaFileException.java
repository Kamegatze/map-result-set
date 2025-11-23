package com.kamegatze.map.result.set.processor.exception;

public class WriteJavaFileException extends RuntimeException {
  public WriteJavaFileException(String message) {
    super(message);
  }

  public WriteJavaFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
