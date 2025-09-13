package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.Processor;
import javax.annotation.processing.RoundEnvironment;

public record GenerateImplementationMapResultSetProcessor(
        GenerateImplementationMapResultSetService generateImplementationMapResultSetService,
        RoundEnvironment roundEnv)
        implements Processor {

    @Override
    public boolean processor() {
        roundEnv.getElementsAnnotatedWith(MapResultSet.class)
                .forEach(
                        it -> {
                            var javaFile = generateImplementationMapResultSetService.generate(it);
                            generateImplementationMapResultSetService.write(javaFile);
                        });
        return true;
    }
}
