package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy.Policy;
import io.github.kamegatze.map.result.set.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Configuration;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public record ConfigurationImpl(
    Element element, Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap)
    implements Configuration {

  @Override
  public Policy conventionFieldPolicy() {
    if (Objects.isNull(element)) {
      return Policy.SNAKE_CASE;
    }
    return element.getAnnotation(MapResultSetConfiguration.class).conventionFieldPolicy();
  }
}
