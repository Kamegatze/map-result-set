package io.github.kamegatze.map.result.set.processor.exception;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class NoSuchElementException extends AnnotationProcessingException {
  public NoSuchElementException(String message) {
    super(message);
  }

  public NoSuchElementException(String message, Element element) {
    super(message, element);
  }

  public NoSuchElementException(
      String message, Element element, AnnotationMirror annotationMirror) {
    super(message, element, annotationMirror);
  }

  public NoSuchElementException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    super(message, element, annotationMirror, annotationValue);
  }

  public NoSuchElementException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public NoSuchElementException(String message, Element element, Throwable throwable) {
    super(message, element, throwable);
  }

  public NoSuchElementException(
      String message, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    super(message, element, annotationMirror, throwable);
  }

  public NoSuchElementException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    super(message, element, annotationMirror, annotationValue, throwable);
  }
}
