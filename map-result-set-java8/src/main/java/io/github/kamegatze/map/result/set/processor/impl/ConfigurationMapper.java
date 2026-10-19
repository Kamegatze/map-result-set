package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Configuration;
import io.github.kamegatze.map.result.set.processor.Mapper;
import io.github.kamegatze.map.result.set.processor.exception.MoreThenOneItemMapResultSetConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public final class ConfigurationMapper implements Mapper<RoundEnvironment, Configuration> {

  @Override
  public Configuration map(RoundEnvironment roundEnvironment) {
    Set<? extends Element> globalConfiguration =
        roundEnvironment.getElementsAnnotatedWith(MapResultSetConfiguration.class);
    if (globalConfiguration.size() > 1) {
      List<? extends Element> listGlobalConfiguration = new ArrayList<>(globalConfiguration);
      throw new MoreThenOneItemMapResultSetConfigurationException(
          "Only one class allowed for annotation @" + MapResultSetConfiguration.class.getName(),
          listGlobalConfiguration.get(listGlobalConfiguration.size() - 1));
    }

    Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap = new HashMap<>();

    roundEnvironment
        .getElementsAnnotatedWith(ConventionFieldPolicy.class)
        .forEach(
            it ->
                conventionFieldPolicyMap.put(
                    it.asType(), it.getAnnotation(ConventionFieldPolicy.class)));

    return new ConfigurationImpl(
        globalConfiguration.stream().findFirst().orElse(null), conventionFieldPolicyMap);
  }
}
