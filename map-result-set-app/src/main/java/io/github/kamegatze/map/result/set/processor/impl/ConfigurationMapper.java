package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.MapResultSetConfiguration;
import io.github.kamegatze.map.result.set.processor.Configuration;
import io.github.kamegatze.map.result.set.processor.Mapper;
import io.github.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import java.util.HashMap;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.type.TypeMirror;

public final class ConfigurationMapper implements Mapper<RoundEnvironment, Configuration> {

  @Override
  public Configuration map(RoundEnvironment roundEnvironment) {
    var globalConfiguration =
        roundEnvironment.getElementsAnnotatedWith(MapResultSetConfiguration.class);
    if (globalConfiguration.size() > 1) {
      throw new MoreThenOneItemException(
          "Only one class allowed for annotation @" + MapResultSetConfiguration.class.getName());
    }

    var conventionFieldPolicyMap = new HashMap<TypeMirror, ConventionFieldPolicy>();

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
