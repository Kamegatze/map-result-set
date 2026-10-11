package io.github.kamegatze.map.result.set.processor.impl;

import com.squareup.javapoet.JavaFile;
import io.github.kamegatze.map.result.set.MapResultSet;
import io.github.kamegatze.map.result.set.logger.Logger;
import io.github.kamegatze.map.result.set.logger.LoggerFactory;
import io.github.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import io.github.kamegatze.map.result.set.processor.Processor;
import javax.annotation.processing.RoundEnvironment;

public final class GenerateImplementationMapResultSetProcessor implements Processor {
  private final GenerateImplementationMapResultSetService generateImplementationMapResultSetService;
  private final RoundEnvironment roundEnv;

  private final Logger log;

  public GenerateImplementationMapResultSetProcessor(
      GenerateImplementationMapResultSetService generateImplementationMapResultSetService,
      RoundEnvironment roundEnv) {
    this.generateImplementationMapResultSetService = generateImplementationMapResultSetService;
    this.roundEnv = roundEnv;
    log = LoggerFactory.create(GenerateImplementationMapResultSetProcessor.class);
  }

  @Override
  public boolean processor() {
    roundEnv
        .getElementsAnnotatedWith(MapResultSet.class)
        .forEach(
            it -> {
              log.info("Generate implementation for {0}", it.asType());
              JavaFile javaFile = generateImplementationMapResultSetService.generate(it);
              log.info("Write file by package: {0}", javaFile.toJavaFileObject().getName());
              log.debug("Content writing in java file: {0}", javaFile);
              generateImplementationMapResultSetService.write(javaFile, it);
              log.info("Write success");
            });
    return true;
  }
}
