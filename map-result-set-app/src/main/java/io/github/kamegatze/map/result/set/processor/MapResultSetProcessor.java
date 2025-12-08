package io.github.kamegatze.map.result.set.processor;

import io.github.kamegatze.map.result.set.context.Context;
import io.github.kamegatze.map.result.set.processor.impl.ClassTreeServiceImpl;
import io.github.kamegatze.map.result.set.processor.impl.GenerateImplementationMapResultSetProcessor;
import io.github.kamegatze.map.result.set.processor.impl.GenerateImplementationMapResultSetServiceImpl;
import io.github.kamegatze.map.result.set.processor.impl.GenerateResultSetMapperImpl;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value = {"io.github.kamegatze.map.result.set.MapResultSet"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class MapResultSetProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Context.set(processingEnv);
    var classTreeService = new ClassTreeServiceImpl(processingEnv);
    return new GenerateImplementationMapResultSetProcessor(
            new GenerateImplementationMapResultSetServiceImpl(
                processingEnv,
                new GenerateResultSetMapperImpl(processingEnv, classTreeService),
                classTreeService),
            roundEnv)
        .processor();
  }
}
