package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy.Policy;
import io.github.kamegatze.map.result.set.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Configuration;
import io.github.kamegatze.map.result.set.processor.GenerateNewObject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public final class ConfigurationImpl implements Configuration {
  private final Element element;
  private final Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap;

  public ConfigurationImpl(
      Element element, Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap) {
    this.element = element;
    this.conventionFieldPolicyMap = conventionFieldPolicyMap;
  }

  @Override
  public Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap() {
    return conventionFieldPolicyMap;
  }

  @Override
  public Policy conventionFieldPolicy() {
    if (Objects.isNull(element)) {
      return Policy.SNAKE_CASE;
    }
    return Objects.requireNonNull(element.getAnnotation(MapResultSetConfiguration.class))
        .conventionFieldPolicy();
  }

  @Override
  public List<GenerateNewObject> generateNewObjects() {
    return StreamSupport.stream(
            ServiceLoader.load(GenerateNewObject.class, GenerateNewObject.class.getClassLoader())
                .spliterator(),
            false)
        .collect(Collectors.toList());
  }
}
