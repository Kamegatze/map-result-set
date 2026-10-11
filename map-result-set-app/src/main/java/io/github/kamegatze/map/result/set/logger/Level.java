package io.github.kamegatze.map.result.set.logger;

import javax.tools.Diagnostic;

public enum Level {
  ERROR(Diagnostic.Kind.ERROR, 400),
  WARNING(Diagnostic.Kind.WARNING, 300),
  INFO(Diagnostic.Kind.NOTE, 200),
  DEBUG(Diagnostic.Kind.OTHER, 100);

  private final Diagnostic.Kind kind;
  private final int priority;

  Level(Diagnostic.Kind kind, int priority) {
    this.kind = kind;
    this.priority = priority;
  }

  public Diagnostic.Kind getKind() {
    return kind;
  }

  public int getPriority() {
    return priority;
  }
}
