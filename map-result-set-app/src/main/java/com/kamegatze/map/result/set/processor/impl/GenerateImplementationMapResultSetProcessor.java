package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.logger.LoggerImpl;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.Processor;
import javax.annotation.processing.RoundEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record GenerateImplementationMapResultSetProcessor(
        GenerateImplementationMapResultSetService generateImplementationMapResultSetService,
        RoundEnvironment roundEnv)
        implements Processor {

    private static final Logger log =
            new LoggerImpl(
                    LoggerFactory.getLogger(GenerateImplementationMapResultSetProcessor.class));

    @Override
    public boolean processor() {
        roundEnv.getElementsAnnotatedWith(MapResultSet.class)
                .forEach(
                        it -> {
                            log.info("Generate implementation for {}", it.asType());
                            var javaFile = generateImplementationMapResultSetService.generate(it);
                            log.info(
                                    "Write file by package: {}",
                                    javaFile.toJavaFileObject().getName());
                            log.debug("Content writing in java file: {}", javaFile);
                            generateImplementationMapResultSetService.write(javaFile);
                            log.info("Write success");
                        });
        return true;
    }
}
