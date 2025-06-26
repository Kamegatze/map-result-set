package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.processor.ProcessorAnnotation;
import com.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import com.palantir.javapoet.*;
import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import org.springframework.jdbc.core.RowMapper;

public final class ProcessorAnnotationImpl implements ProcessorAnnotation {

    private final ProcessingEnvironment processingEnvironment;

    public ProcessorAnnotationImpl(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
    }

    @Override
    public JavaFile processor(Element element) {

        var methodSpecs =
                element.getEnclosedElements().stream()
                        .filter(it -> it.getKind().equals(ElementKind.METHOD))
                        .map(this::createMethod)
                        .toList();

        var typeSpec =
                TypeSpec.classBuilder("%s%s".formatted(element.getSimpleName().toString(), "Impl"))
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(element.asType())
                        .addMethods(methodSpecs)
                        .build();
        var packageElement = processingEnvironment.getElementUtils().getPackageOf(element);

        return JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec).build();
    }

    @Override
    public void write(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnvironment.getFiler());
        } catch (IOException e) {
            throw new WriteJavaFileException("Error write java file", e);
        }
    }

    private MethodSpec createMethod(Element element) {
        if (element instanceof ExecutableElement executableElement) {
            var returnType = TypeName.get(executableElement.getReturnType());

            var codeBlock =
                    returnType.equals(TypeName.get(RowMapper.class))
                            ? createRowMapper()
                            : extractResultSet();

            return MethodSpec.methodBuilder(element.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType)
                    .addStatement(codeBlock)
                    .build();
        }
        throw new ClassCastException(
                "%s is not %s"
                        .formatted(Element.class.toString(), ExecutableElement.class.toString()));
    }

    private CodeBlock createRowMapper() {
        return null;
    }

    private CodeBlock extractResultSet() {
        return null;
    }
}
