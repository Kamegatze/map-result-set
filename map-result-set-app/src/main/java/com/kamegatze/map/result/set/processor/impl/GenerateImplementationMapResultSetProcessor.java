package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.Processor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public final class GenerateImplementationMapResultSetProcessor implements Processor {

    private final GenerateImplementationMapResultSetService
            generateImplementationMapResultSetService;
    private final RoundEnvironment roundEnv;

    public GenerateImplementationMapResultSetProcessor(
            ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;
        var classTreeService = new ClassTreeServiceImpl(processingEnv);
        this.generateImplementationMapResultSetService =
                new GenerateImplementationMapResultSetServiceImpl(
                        processingEnv,
                        new GenerateRowMapperImpl(processingEnv, classTreeService),
                        classTreeService);
    }

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
