package io.github.kamegatze.map.result.set.processor.exception;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class MoreThenOneItemMapResultSetConfigurationException
    extends AnnotationProcessingException {
  public MoreThenOneItemMapResultSetConfigurationException(String message) {
    super(message);
  }

  public MoreThenOneItemMapResultSetConfigurationException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    super(message, element, annotationMirror, annotationValue, throwable);
  }

  public MoreThenOneItemMapResultSetConfigurationException(
      String message, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    super(message, element, annotationMirror, throwable);
  }

  public MoreThenOneItemMapResultSetConfigurationException(
      String message, Element element, Throwable throwable) {
    super(message, element, throwable);
  }

  public MoreThenOneItemMapResultSetConfigurationException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public MoreThenOneItemMapResultSetConfigurationException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    super(message, element, annotationMirror, annotationValue);
  }

  public MoreThenOneItemMapResultSetConfigurationException(
      String message, Element element, AnnotationMirror annotationMirror) {
    super(message, element, annotationMirror);
  }

  public MoreThenOneItemMapResultSetConfigurationException(String message, Element element) {
    super(message, element);
  }
}
