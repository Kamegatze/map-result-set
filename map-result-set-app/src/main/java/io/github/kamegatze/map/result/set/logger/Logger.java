package io.github.kamegatze.map.result.set.logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public interface Logger {

  boolean isLoggable(Level level);

  String getName();

  void debug(String msg);

  void debug(String msg, Object... params);

  void debug(String msg, Element element);

  void debug(String msg, Element element, Object... params);

  void debug(String msg, Element element, AnnotationMirror annotationMirror);

  void debug(String msg, Element element, AnnotationMirror annotationMirror, Object... params);

  void debug(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue);

  void debug(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params);

  void info(String msg);

  void info(String msg, Object... params);

  void info(String msg, Element element);

  void info(String msg, Element element, Object... params);

  void info(String msg, Element element, AnnotationMirror annotationMirror);

  void info(String msg, Element element, AnnotationMirror annotationMirror, Object... params);

  void info(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue);

  void info(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params);

  void warning(String msg);

  void warning(String msg, Object... params);

  void warning(String msg, Element element);

  void warning(String msg, Element element, Object... params);

  void warning(String msg, Element element, AnnotationMirror annotationMirror);

  void warning(String msg, Element element, AnnotationMirror annotationMirror, Object... params);

  void warning(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue);

  void warning(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params);

  void error(String msg);

  void error(String msg, Object... params);

  void error(String msg, Element element);

  void error(String msg, Element element, Object... params);

  void error(String msg, Element element, AnnotationMirror annotationMirror);

  void error(String msg, Element element, AnnotationMirror annotationMirror, Object... params);

  void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue);

  void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params);

  void error(String msg, Throwable throwable);

  void error(String msg, Element element, Throwable throwable);

  void error(String msg, Element element, AnnotationMirror annotationMirror, Throwable throwable);

  void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable);
}
