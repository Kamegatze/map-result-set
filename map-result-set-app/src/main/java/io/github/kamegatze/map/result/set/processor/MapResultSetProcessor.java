package io.github.kamegatze.map.result.set.processor;

import io.github.kamegatze.map.result.set.context.Context;
import io.github.kamegatze.map.result.set.logger.Logger;
import io.github.kamegatze.map.result.set.logger.LoggerFactory;
import io.github.kamegatze.map.result.set.processor.exception.RegisterException;
import io.github.kamegatze.map.result.set.processor.impl.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value = {"io.github.kamegatze.map.result.set.MapResultSet"})
public final class MapResultSetProcessor extends AbstractProcessor {

  private final Mapper<RoundEnvironment, Configuration> configurationMapper =
      new ConfigurationMapper();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Context.set(processingEnv);
    Callable<Boolean> callable =
        () -> {
          Configuration configuration = configurationMapper.map(roundEnv);
          Context.set(configuration);
          ClassTreeService classTreeService = new ClassTreeServiceImpl(processingEnv);
          return new GenerateImplementationMapResultSetProcessor(
                  new GenerateImplementationMapResultSetServiceImpl(
                      processingEnv,
                      new GenerateResultSetMapperImpl(
                          createGenerateNewObjectForKinds(configuration)),
                      classTreeService),
                  roundEnv)
              .processor();
        };
    return Boolean.TRUE.equals(new ErrorHandlerImpl<Boolean>().handleCallable(callable));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private Map<ElementKind, GenerateNewObject> createGenerateNewObjectForKinds(
      Configuration configuration) {
    final Logger logger = LoggerFactory.create(MapResultSetProcessor.class);
    try {
      Map<ElementKind, GenerateNewObject> generateNewObjectMap = new EnumMap<>(ElementKind.class);
      GenerateNewObject generateNewObjectClass = new GenerateNewObjectClass();
      generateNewObjectMap.put(generateNewObjectClass.type(), generateNewObjectClass);
      configuration.generateNewObjects().forEach(it -> generateNewObjectMap.put(it.type(), it));
      logger.info(
          "register generate new object: {0}, register value: {1}",
          generateNewObjectMap.keySet(), configuration.generateNewObjects());
      return Collections.unmodifiableMap(generateNewObjectMap);
    } catch (Exception e) {
      throw new RegisterException("Error create object by register generate new object", e);
    }
  }
}
