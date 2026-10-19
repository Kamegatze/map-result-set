package io.github.kamegatze.map.result.set.processor.exception;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class UnsupportedTypeException extends AnnotationProcessingException {
  public UnsupportedTypeException(String message) {
    super(message);
  }

  public UnsupportedTypeException(String message, Element element) {
    super(message, element);
  }

  public UnsupportedTypeException(
      String message, Element element, AnnotationMirror annotationMirror) {
    super(message, element, annotationMirror);
  }

  public UnsupportedTypeException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    super(message, element, annotationMirror, annotationValue);
  }

  public UnsupportedTypeException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public UnsupportedTypeException(String message, Element element, Throwable throwable) {
    super(message, element, throwable);
  }

  public UnsupportedTypeException(
      String message, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    super(message, element, annotationMirror, throwable);
  }

  public UnsupportedTypeException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    super(message, element, annotationMirror, annotationValue, throwable);
  }
}
