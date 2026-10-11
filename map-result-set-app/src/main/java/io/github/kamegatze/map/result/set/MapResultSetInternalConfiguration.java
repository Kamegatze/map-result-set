package io.github.kamegatze.map.result.set;

import io.github.kamegatze.map.result.set.processor.GenerateNewObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MapResultSetInternalConfiguration {
  Class<? extends GenerateNewObject>[] generateNewObjects() default {};
}
