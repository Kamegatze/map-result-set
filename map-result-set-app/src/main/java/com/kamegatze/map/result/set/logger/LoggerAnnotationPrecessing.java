package com.kamegatze.map.result.set.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public final class LoggerAnnotationPrecessing implements System.Logger {

  private final String name;
  private final Level level;
  private final ProcessingEnvironment processingEnv;

  public LoggerAnnotationPrecessing(String name, Level level, ProcessingEnvironment processingEnv) {
    this.name = name;
    this.level = level;
    this.processingEnv = processingEnv;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isLoggable(Level level) {
    return level.getSeverity() >= this.level.getSeverity();
  }

  @Override
  public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
    if (!this.isLoggable(level)) {
      return;
    }
    var stringWriter = new StringWriter();
    var errorWriter = new PrintWriter(stringWriter);
    thrown.printStackTrace(errorWriter);
    if (Objects.nonNull(bundle) && bundle.containsKey(msg)) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.NOTE,
              formatting(
                  "{0} {1} {2} - {3}\n{4}",
                  LocalDateTime.now(), level.getName(), name, bundle.getString(msg), stringWriter));
      return;
    }
    processingEnv
        .getMessager()
        .printMessage(
            Diagnostic.Kind.NOTE,
            formatting(
                "{0} {1} {2} - {3}\n{4}",
                LocalDateTime.now(), level.getName(), name, msg, stringWriter));
  }

  @Override
  public void log(Level level, ResourceBundle bundle, String format, Object... params) {

    if (!this.isLoggable(level)) {
      return;
    }

    if (Objects.nonNull(bundle) && bundle.containsKey(format)) {
      var formatFromBundle = bundle.getString(format);
      if (Objects.nonNull(params)) {
        for (int i = 0; i < params.length; i++) {
          formatFromBundle = formatFromBundle.replace("{" + i + "}", String.valueOf(params[i]));
        }
      }
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.NOTE,
              formatting(
                  "{0} {1} {2} - {3}",
                  LocalDateTime.now(),
                  level.getName(),
                  name,
                  formatting(formatFromBundle, params)));
      return;
    }

    processingEnv
        .getMessager()
        .printMessage(
            Diagnostic.Kind.NOTE,
            formatting(
                "{0} {1} {2} - {3}",
                LocalDateTime.now(), level.getName(), name, formatting(format, params)));
  }

  private String formatting(String format, Object... params) {
    if (Objects.isNull(params)) {
      return format;
    }
    var result = format;
    for (int i = 0; i < params.length; i++) {
      result = result.replace("{" + i + "}", String.valueOf(params[i]));
    }
    return result;
  }
}
