package io.github.kamegatze.map.result.set.processor.exception;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public abstract class AnnotationProcessingException extends RuntimeException {

  private final transient Element element;
  private final transient AnnotationMirror annotationMirror;
  private final transient AnnotationValue annotationValue;

  public Element getElement() {
    return this.element;
  }

  public AnnotationMirror getAnnotationMirror() {
    return this.annotationMirror;
  }

  public AnnotationValue getAnnotationValue() {
    return this.annotationValue;
  }

  protected AnnotationProcessingException(String message) {
    super(message);
    this.element = null;
    this.annotationMirror = null;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(String message, Element element) {
    super(message);
    this.element = element;
    this.annotationMirror = null;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(
      String message, Element element, AnnotationMirror annotationMirror) {
    super(message);
    this.element = element;
    this.annotationMirror = annotationMirror;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    super(message);
    this.element = element;
    this.annotationMirror = annotationMirror;
    this.annotationValue = annotationValue;
  }

  protected AnnotationProcessingException(String message, Throwable throwable) {
    super(message, throwable);
    this.element = null;
    this.annotationMirror = null;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(String message, Element element, Throwable throwable) {
    super(message, throwable);
    this.element = element;
    this.annotationMirror = null;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(
      String message, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    super(message, throwable);
    this.element = element;
    this.annotationMirror = annotationMirror;
    this.annotationValue = null;
  }

  protected AnnotationProcessingException(
      String message,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    super(message, throwable);
    this.element = element;
    this.annotationMirror = annotationMirror;
    this.annotationValue = annotationValue;
  }
}
