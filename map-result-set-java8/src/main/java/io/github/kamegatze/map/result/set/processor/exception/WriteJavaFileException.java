package io.github.kamegatze.map.result.set.processor.exception;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class WriteJavaFileException extends AnnotationProcessingException {
  public WriteJavaFileException(String message) {
    super(message);
  }

  public WriteJavaFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public WriteJavaFileException(String message, Element element, Throwable throwable) {
    super(message, element, throwable);
  }

  public WriteJavaFileException(
      String message, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    super(message, element, annotationMirror, throwable);
  }

  public WriteJavaFileException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    super(message, element, annotationMirror, annotationValue, throwable);
  }

  public WriteJavaFileException(String message, Element element) {
    super(message, element);
  }

  public WriteJavaFileException(
      String message, Element element, AnnotationMirror annotationMirror) {
    super(message, element, annotationMirror);
  }

  public WriteJavaFileException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    super(message, element, annotationMirror, annotationValue);
  }
}
