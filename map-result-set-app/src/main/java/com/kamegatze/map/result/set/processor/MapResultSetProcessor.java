package com.kamegatze.map.result.set.processor;

import com.kamegatze.map.result.set.processor.impl.GenerateImplementationMapResultSetProcessor;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value = {"com.kamegatze.map.result.set.MapResultSet"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class MapResultSetProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return new GenerateImplementationMapResultSetProcessor(processingEnv, roundEnv).processor();
    }
}
