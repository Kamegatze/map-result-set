package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.logger.LoggerFactory;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.Processor;
import javax.annotation.processing.RoundEnvironment;

public final class GenerateImplementationMapResultSetProcessor implements Processor {
  private final GenerateImplementationMapResultSetService generateImplementationMapResultSetService;
  private final RoundEnvironment roundEnv;

  private final System.Logger log;

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
              log.log(System.Logger.Level.INFO, "Generate implementation for {0}", it.asType());
              var javaFile = generateImplementationMapResultSetService.generate(it);
              log.log(
                  System.Logger.Level.INFO,
                  "Write file by package: {0}",
                  javaFile.toJavaFileObject().getName());
              log.log(System.Logger.Level.DEBUG, "Content writing in java file: {0}", javaFile);
              generateImplementationMapResultSetService.write(javaFile);
              log.log(System.Logger.Level.INFO, "Write success");
            });
    return true;
  }
}
