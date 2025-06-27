package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.Column;
import com.kamegatze.map.result.set.processor.ProcessorAnnotation;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import com.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import com.palantir.javapoet.*;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

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
            throw new WriteJavaFileException(
                    "Error write java file with error message: %s".formatted(e.getMessage()), e);
        }
    }

    private MethodSpec createMethod(Element element) {
        if (element instanceof ExecutableElement executableElement) {
            var returnType = TypeName.get(executableElement.getReturnType());

            var codeBlock = createRowMapper(executableElement.getReturnType());

            return MethodSpec.methodBuilder(element.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType)
                    .addCode(codeBlock)
                    .build();
        }
        throw new ClassCastException(
                "%s not instanceof %s. Impossible create method by element %s"
                        .formatted(
                                Element.class.toString(),
                                ExecutableElement.class.toString(),
                                element.toString()));
    }

    private CodeBlock createRowMapper(TypeMirror typeMirror) {
        var typesUtils = processingEnvironment.getTypeUtils();
        var generic =
                getOneGeneric(typeMirror)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No such generic by %s"
                                                        .formatted(typeMirror.toString())));

        var genericElement = typesUtils.asElement(generic);

        var fieldsName = getFieldsName(generic);

        var builder = CodeBlock.builder();

        if (ElementKind.RECORD.equals(genericElement.getKind())) {

        } else {
            builder.addStatement("var t = new $T()", TypeName.get(generic));
            builder.add("return (rs, rowNum) -> {\n");
            builder.indent();
            fieldsName.forEach(
                    it -> {
                        var fieldName = it.getSimpleName().toString();
                        var methodName = generateSetMethodName(fieldName);
                        builder.addStatement(
                                "t.%s(rs.getObject($S, $T.class))".formatted(methodName),
                                getColumnName(it),
                                TypeName.get(it.asType()));
                    });
            builder.addStatement("return t");
            builder.unindent();
            builder.add("};");
        }

        return builder.build();
    }

    private CodeBlock extractResultSet() {
        return null;
    }

    private List<VariableElement> getFieldsName(TypeMirror typeMirror) {
        var elementsUtils = processingEnvironment.getElementUtils();
        var typesUtils = processingEnvironment.getTypeUtils();

        var element = typesUtils.asElement(typeMirror);
        var packageName = elementsUtils.getPackageOf(element);

        var typeElement =
                elementsUtils.getTypeElement(
                        "%s.%s"
                                .formatted(
                                        packageName.getQualifiedName().toString(),
                                        element.getSimpleName().toString()));

        return ElementFilter.fieldsIn(
                processingEnvironment.getElementUtils().getAllMembers(typeElement));
    }

    private Optional<TypeMirror> getOneGeneric(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType declaredType) {
            var generics = declaredType.getTypeArguments();
            if (generics.size() > 1) {
                throw new MoreThenOneItemException("Generic type more then one item");
            }
            if (generics.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(generics.get(0));
        }
        throw new ClassCastException(
                "%s not instanceof %s. Impossible get generic type from %s"
                        .formatted(
                                TypeMirror.class.toString(),
                                DeclaredType.class.toString(),
                                typeMirror.toString()));
    }

    private String generateSetMethodName(String fieldName) {
        var fieldNameCopy = fieldName.startsWith("is") ? fieldName.substring(2) : fieldName;
        return "set%s"
                .formatted(
                        fieldNameCopy.substring(0, 1).toUpperCase() + fieldNameCopy.substring(1));
    }

    private String getColumnName(VariableElement variableElement) {
        var column = variableElement.getAnnotation(Column.class);
        if (Objects.nonNull(column)) {
            return column.value();
        }
        return variableElement.getSimpleName().toString();
    }
}
