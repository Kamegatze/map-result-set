package io.github.kamegatze.map.result.set.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public final class LoggerImpl implements Logger {

  private static final String TEMPLATE_LOG = "{0} {1} {2} - {3}";
  private static final String TEMPLATE_LOG_THROWABLE = "{0} {1} {2} - {3}\n{4}";

  private final String name;
  private final Level level;
  private final ProcessingEnvironment processingEnv;

  public LoggerImpl(String name, Level level, ProcessingEnvironment processingEnv) {
    this.name = name;
    this.level = level;
    this.processingEnv = processingEnv;
  }

  @Override
  public boolean isLoggable(Level level) {
    return level.getPriority() >= this.level.getPriority();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void debug(String msg) {
    log(Level.DEBUG, msg);
  }

  @Override
  public void debug(String msg, Object... params) {
    log(Level.DEBUG, msg, params);
  }

  @Override
  public void debug(String msg, Element element) {
    log(Level.DEBUG, msg, element);
  }

  @Override
  public void debug(String msg, Element element, Object... params) {
    log(Level.DEBUG, msg, element, params);
  }

  @Override
  public void debug(String msg, Element element, AnnotationMirror annotationMirror) {
    log(Level.DEBUG, msg, element, annotationMirror);
  }

  @Override
  public void debug(
      String msg, Element element, AnnotationMirror annotationMirror, Object... params) {
    log(Level.DEBUG, msg, element, annotationMirror, params);
  }

  @Override
  public void debug(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    log(Level.DEBUG, msg, element, annotationMirror, annotationValue);
  }

  @Override
  public void debug(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params) {
    log(Level.DEBUG, msg, element, annotationMirror, annotationValue, params);
  }

  @Override
  public void info(String msg) {
    log(Level.INFO, msg);
  }

  @Override
  public void info(String msg, Object... params) {
    log(Level.INFO, msg, params);
  }

  @Override
  public void info(String msg, Element element) {
    log(Level.INFO, msg, element);
  }

  @Override
  public void info(String msg, Element element, Object... params) {
    log(Level.INFO, msg, element, params);
  }

  @Override
  public void info(String msg, Element element, AnnotationMirror annotationMirror) {
    log(Level.INFO, msg, element, annotationMirror);
  }

  @Override
  public void info(
      String msg, Element element, AnnotationMirror annotationMirror, Object... params) {
    log(Level.INFO, msg, element, annotationMirror, params);
  }

  @Override
  public void info(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    log(Level.INFO, msg, element, annotationMirror, annotationValue);
  }

  @Override
  public void info(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params) {
    log(Level.INFO, msg, element, annotationMirror, annotationValue, params);
  }

  @Override
  public void warning(String msg) {
    log(Level.WARNING, msg);
  }

  @Override
  public void warning(String msg, Object... params) {
    log(Level.WARNING, msg, params);
  }

  @Override
  public void warning(String msg, Element element) {
    log(Level.WARNING, msg, element);
  }

  @Override
  public void warning(String msg, Element element, Object... params) {
    log(Level.WARNING, msg, element, params);
  }

  @Override
  public void warning(String msg, Element element, AnnotationMirror annotationMirror) {
    log(Level.WARNING, msg, element, annotationMirror);
  }

  @Override
  public void warning(
      String msg, Element element, AnnotationMirror annotationMirror, Object... params) {
    log(Level.WARNING, msg, element, annotationMirror, params);
  }

  @Override
  public void warning(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    log(Level.WARNING, msg, element, annotationMirror, annotationValue);
  }

  @Override
  public void warning(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params) {
    log(Level.WARNING, msg, element, annotationMirror, annotationValue, params);
  }

  @Override
  public void error(String msg) {
    log(Level.ERROR, msg);
  }

  @Override
  public void error(String msg, Object... params) {
    log(Level.ERROR, msg, params);
  }

  @Override
  public void error(String msg, Element element) {
    log(Level.ERROR, msg, element);
  }

  @Override
  public void error(String msg, Element element, Object... params) {
    log(Level.ERROR, msg, element, params);
  }

  @Override
  public void error(String msg, Element element, AnnotationMirror annotationMirror) {
    log(Level.ERROR, msg, element, annotationMirror);
  }

  @Override
  public void error(
      String msg, Element element, AnnotationMirror annotationMirror, Object... params) {
    log(Level.ERROR, msg, element, annotationMirror, params);
  }

  @Override
  public void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue) {
    log(Level.ERROR, msg, element, annotationMirror, annotationValue);
  }

  @Override
  public void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params) {
    log(Level.ERROR, msg, element, annotationMirror, annotationValue, params);
  }

  @Override
  public void error(String msg, Throwable throwable) {
    log(Level.ERROR, msg, throwable);
  }

  @Override
  public void error(String msg, Element element, Throwable throwable) {
    log(Level.ERROR, msg, element, throwable);
  }

  @Override
  public void error(
      String msg, Element element, AnnotationMirror annotationMirror, Throwable throwable) {
    log(Level.ERROR, msg, element, annotationMirror, throwable);
  }

  @Override
  public void error(
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable throwable) {
    log(Level.ERROR, msg, element, annotationMirror, annotationValue, throwable);
  }

  private void log(Level level, String msg, Throwable thrown) {
    if (!this.isLoggable(level)) {
      return;
    }
    StringWriter stringWriter = new StringWriter();
    PrintWriter errorWriter = new PrintWriter(stringWriter);
    thrown.printStackTrace(errorWriter);

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG_THROWABLE,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                msg,
                stringWriter));
  }

  private void log(Level level, String msg, Element element, Throwable thrown) {
    if (!this.isLoggable(level)) {
      return;
    }
    StringWriter stringWriter = new StringWriter();
    PrintWriter errorWriter = new PrintWriter(stringWriter);
    thrown.printStackTrace(errorWriter);

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG_THROWABLE,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                msg,
                stringWriter),
            element);
  }

  private void log(
      Level level,
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      Throwable thrown) {
    if (!this.isLoggable(level)) {
      return;
    }
    StringWriter stringWriter = new StringWriter();
    PrintWriter errorWriter = new PrintWriter(stringWriter);
    thrown.printStackTrace(errorWriter);

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG_THROWABLE,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                msg,
                stringWriter),
            element,
            annotationMirror);
  }

  private void log(
      Level level,
      String msg,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Throwable thrown) {
    if (!this.isLoggable(level)) {
      return;
    }
    StringWriter stringWriter = new StringWriter();
    PrintWriter errorWriter = new PrintWriter(stringWriter);
    thrown.printStackTrace(errorWriter);

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG_THROWABLE,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                msg,
                stringWriter),
            element,
            annotationMirror,
            annotationValue);
  }

  private void log(Level level, String format, Object... params) {

    if (!this.isLoggable(level)) {
      return;
    }

    if (params.length > 0 && params[params.length - 1] instanceof Throwable) {
      Object[] paramsCopyWithoutThrowable = Arrays.copyOf(params, params.length - 1);
      log(
          level,
          formatting(format, paramsCopyWithoutThrowable),
          (Throwable) params[params.length - 1]);
      return;
    }

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                formatting(format, params)));
  }

  private void log(Level level, String format, Element element, Object... params) {

    if (!this.isLoggable(level)) {
      return;
    }

    if (params.length > 0 && params[params.length - 1] instanceof Throwable) {
      Object[] paramsCopyWithoutThrowable = Arrays.copyOf(params, params.length - 1);
      log(
          level,
          formatting(format, paramsCopyWithoutThrowable),
          element,
          (Throwable) params[params.length - 1]);
      return;
    }

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                formatting(format, params)),
            element);
  }

  private void log(
      Level level,
      String format,
      Element element,
      AnnotationMirror annotationMirror,
      Object... params) {

    if (!this.isLoggable(level)) {
      return;
    }

    if (params.length > 0 && params[params.length - 1] instanceof Throwable) {
      Object[] paramsCopyWithoutThrowable = Arrays.copyOf(params, params.length - 1);
      log(
          level,
          formatting(format, paramsCopyWithoutThrowable),
          element,
          annotationMirror,
          (Throwable) params[params.length - 1]);
      return;
    }
    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                formatting(format, params)),
            element,
            annotationMirror);
  }

  private void log(
      Level level,
      String format,
      Element element,
      AnnotationMirror annotationMirror,
      AnnotationValue annotationValue,
      Object... params) {

    if (!this.isLoggable(level)) {
      return;
    }

    if (params.length > 0 && params[params.length - 1] instanceof Throwable) {
      Object[] paramsCopyWithoutThrowable = Arrays.copyOf(params, params.length - 1);
      log(
          level,
          formatting(format, paramsCopyWithoutThrowable),
          element,
          annotationMirror,
          annotationValue,
          (Throwable) params[params.length - 1]);
      return;
    }

    processingEnv
        .getMessager()
        .printMessage(
            level.getKind(),
            formatting(
                TEMPLATE_LOG,
                LocalDateTime.now(ZoneId.systemDefault()),
                level,
                name,
                formatting(format, params)),
            element,
            annotationMirror,
            annotationValue);
  }

  private String formatting(String format, Object... params) {
    if (Objects.isNull(params) || params.length == 0) {
      return format;
    }
    String result = format;
    for (int i = 0; i < params.length; i++) {
      result = result.replace("{" + i + "}", String.valueOf(params[i]));
    }
    return result;
  }
}
