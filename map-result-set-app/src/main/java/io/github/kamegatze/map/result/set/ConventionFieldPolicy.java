package io.github.kamegatze.map.result.set;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface ConventionFieldPolicy {
  enum Policy {
    CAMEL_CASE,
    SNAKE_CASE,
    PASCAL_CASE
  }

  Policy value() default Policy.SNAKE_CASE;
}
