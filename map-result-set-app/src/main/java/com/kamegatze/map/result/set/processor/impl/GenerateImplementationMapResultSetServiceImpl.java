package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.GenerateRowMapper;
import com.kamegatze.map.result.set.processor.exception.ExtractResultSetException;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import com.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import com.kamegatze.map.result.set.processor.utilities.CodeUtility;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.TypeVariableName;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import org.springframework.jdbc.core.RowMapper;

public record GenerateImplementationMapResultSetServiceImpl(
        ProcessingEnvironment processingEnvironment,
        GenerateRowMapper generateRowMapper,
        ClassTreeService classTreeService)
        implements GenerateImplementationMapResultSetService {

    @Override
    public JavaFile generate(Element element) {

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
                        .addMethod(createExtractMethod())
                        .addMethod(createExtractMethodOne())
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

    private MethodSpec createExtractMethod() {
        var generic = TypeVariableName.get("T");
        return MethodSpec.methodBuilder(CodeUtility.EXTRACT_ROW_MAPPER)
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(generic)
                .addException(SQLException.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(RowMapper.class), generic),
                        "rowMapper")
                .addParameter(ResultSet.class, "rs")
                .addStatement(
                        "var list = new $T()",
                        ParameterizedTypeName.get(ClassName.get(ArrayList.class), generic))
                .beginControlFlow("while(rs.next())")
                .addStatement("list.add(rowMapper.mapRow(rs, rs.getRow()))")
                .endControlFlow()
                .addStatement("return list")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), generic))
                .build();
    }

    private MethodSpec createExtractMethodOne() {
        var generic = TypeVariableName.get("T");
        return MethodSpec.methodBuilder(CodeUtility.EXTRACT_ROW_MAPPER_ONE)
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(generic)
                .addException(SQLException.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(RowMapper.class), generic),
                        "rowMapper")
                .addParameter(ResultSet.class, "rs")
                .addStatement(
                        "var list = %s(rowMapper, rs)".formatted(CodeUtility.EXTRACT_ROW_MAPPER))
                .beginControlFlow("if(list.isEmpty())")
                .addStatement("return null")
                .endControlFlow()
                .beginControlFlow("if(list.size() > 1)")
                .addStatement(
                        "throw new $T($S)",
                        ClassName.get(MoreThenOneItemException.class),
                        "ResultSet more then one item")
                .endControlFlow()
                .addStatement("return list.get(0)")
                .returns(generic)
                .build();
    }

    private MethodSpec createMethod(Element element) {
        if (element instanceof ExecutableElement executableElement) {
            var returnType = TypeName.get(executableElement.getReturnType());

            var codeBlocks = createCodeBlock(executableElement.getReturnType(), executableElement);

            var argument =
                    executableElement.getParameters().stream().map(ParameterSpec::get).toList();

            var methodBuilder = MethodSpec.methodBuilder(element.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameters(argument)
                    .returns(returnType);

            codeBlocks.forEach(methodBuilder::addCode);

            return methodBuilder.build();
        }
        throw new ClassCastException(
                "%s not instanceof %s. Impossible create method by element %s"
                        .formatted(
                                Element.class.toString(),
                                ExecutableElement.class.toString(),
                                element.toString()));
    }

    private List<CodeBlock> createCodeBlock(
            TypeMirror typeMirror, ExecutableElement executableElement) {
        var element = processingEnvironment.getTypeUtils().asElement(typeMirror);
        var packageOf = processingEnvironment.getElementUtils().getPackageOf(element);

        var canonicalName =
                packageOf.getQualifiedName().toString() + "." + element.getSimpleName().toString();

        if (canonicalName.equals(RowMapper.class.getCanonicalName())) {
            return createRowMapper(typeMirror);
        }
        return extractObject(
                typeMirror, variableName(executableElement, ResultSet.class.getCanonicalName()));
    }

    private String variableName(ExecutableElement executableElement, String canonicalNameClass) {
        return executableElement.getParameters().stream()
                .filter(
                        it -> {
                            var element =
                                    processingEnvironment.getTypeUtils().asElement(it.asType());
                            var packageOf =
                                    processingEnvironment.getElementUtils().getPackageOf(element);
                            var canonicalName =
                                    packageOf.getQualifiedName().toString()
                                            + "."
                                            + element.getSimpleName().toString();
                            return canonicalName.equals(canonicalNameClass);
                        })
                .map(it -> it.getSimpleName().toString())
                .findFirst()
                .orElseThrow(
                        () -> new NoSuchElementException("No such variable name for ResultSet"));
    }

    private List<CodeBlock> createRowMapper(TypeMirror typeMirror) {
        var generic =
                CodeUtility.getOneGeneric(typeMirror)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No such generic by %s"
                                                        .formatted(typeMirror.toString())));

        var fieldsName = CodeUtility.getFieldsName(generic, processingEnvironment);

        var root = classTreeService.createTree(fieldsName, generic);
        return List.of(
                generateRowMapper.generate(root),
                CodeBlock.builder()
                        .addStatement(
                                CodeUtility.RETURN_TEMPLATE.formatted(
                                        CodeUtility.ROOT_VARIABLE_ROW_MAPPER))
                        .build());
    }

    private List<CodeBlock> extractObject(TypeMirror typeMirror, String nameVariableResultSet) {
        var genericOptional = CodeUtility.getOneGeneric(typeMirror);

        var fieldsName =
                CodeUtility.getFieldsName(
                        genericOptional.orElse(typeMirror), processingEnvironment);

        var methodNameExtract =
                genericOptional.isPresent()
                        ? CodeUtility.EXTRACT_ROW_MAPPER
                        : CodeUtility.EXTRACT_ROW_MAPPER_ONE;

        var root = classTreeService.createTree(fieldsName, genericOptional.orElse(typeMirror));
        return List.of(
                generateRowMapper.generate(root),
                CodeBlock.builder()
                        .beginControlFlow("try")
                        .addStatement(
                                "return %s(%s, %s)"
                                        .formatted(
                                                methodNameExtract,
                                                CodeUtility.ROOT_VARIABLE_ROW_MAPPER,
                                                nameVariableResultSet))
                        .endControlFlow()
                        .beginControlFlow("catch($T e)", SQLException.class)
                        .addStatement(
                                "throw new $T($S, e)",
                                ExtractResultSetException.class,
                                "Error extract from ResultSet")
                        .endControlFlow()
                        .build());
    }
}
