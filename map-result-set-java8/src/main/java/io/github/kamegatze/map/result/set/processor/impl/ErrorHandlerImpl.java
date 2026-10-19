package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.logger.Logger;
import io.github.kamegatze.map.result.set.logger.LoggerFactory;
import io.github.kamegatze.map.result.set.processor.ErrorHandler;
import io.github.kamegatze.map.result.set.processor.exception.AnnotationProcessingException;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class ErrorHandlerImpl<T> implements ErrorHandler<T> {

  private final Logger log;

  public ErrorHandlerImpl() {
    this.log = LoggerFactory.create(ErrorHandlerImpl.class);
  }

  @Override
  public T handleCallable(Callable<T> callable) {
    try {
      return callable.call();
    } catch (AnnotationProcessingException e) {
      if (Objects.nonNull(e.getElement())
          && Objects.isNull(e.getAnnotationMirror())
          && Objects.isNull(e.getAnnotationValue())) {
        log.error(e.getMessage(), e.getElement(), e);
        return null;
      }
      if (Objects.nonNull(e.getElement())
          && Objects.nonNull(e.getAnnotationMirror())
          && Objects.isNull(e.getAnnotationValue())) {
        log.error(e.getMessage(), e.getElement(), e.getAnnotationMirror(), e);
        return null;
      }
      if (Objects.nonNull(e.getElement())
          && Objects.nonNull(e.getAnnotationMirror())
          && Objects.nonNull(e.getAnnotationValue())) {
        log.error(
            e.getMessage(), e.getElement(), e.getAnnotationMirror(), e.getAnnotationValue(), e);
        return null;
      }
      log.error(e.getMessage(), e);
      return null;
    } catch (Exception e) {
      log.error("Unexpected error during annotation processing", e);
      return null;
    }
  }
}
