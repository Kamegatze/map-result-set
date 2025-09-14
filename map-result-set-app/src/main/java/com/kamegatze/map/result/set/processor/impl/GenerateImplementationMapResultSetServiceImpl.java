package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.logger.LoggerImpl;
import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.GenerateRowMapper;
import com.kamegatze.map.result.set.processor.exception.ExtractResultSetException;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import com.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import com.kamegatze.map.result.set.processor.utilities.CodeUtility;
import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public record GenerateImplementationMapResultSetServiceImpl(
        ProcessingEnvironment processingEnvironment,
        GenerateRowMapper generateRowMapper,
        ClassTreeService classTreeService)
        implements GenerateImplementationMapResultSetService {

    private static final Logger log =
            new LoggerImpl(
                    LoggerFactory.getLogger(GenerateImplementationMapResultSetServiceImpl.class));

    @Override
    public JavaFile generate(Element element) {

        var methodSpecs =
                element.getEnclosedElements().stream()
                        .filter(it -> it.getKind().equals(ElementKind.METHOD))
                        .map(
                                it -> {
                                    var signatureOfMethod =
                                            it.getSimpleName() + it.asType().toString();

                                    log.info(
                                            "Begin operation create method: {}", signatureOfMethod);
                                    var method = createMethod(it);
                                    log.info("End operation create method: {}", signatureOfMethod);
                                    return method;
                                })
                        .toList();

        var typeSpec =
                TypeSpec.classBuilder(
                                element.getSimpleName().toString()
                                        + GeneralConstantUtility.POSTFIX_IMPLEMENT_INTERFACE)
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
                    "Error write java file with error message: " + e.getMessage(), e);
        }
    }

    private MethodSpec createExtractMethod() {
        var generic = TypeVariableName.get("T");
        return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_ROW_MAPPER)
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(generic)
                .addException(SQLException.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(RowMapper.class), generic),
                        GeneralConstantUtility.EXTRACT_ARG_ROW_MAPPER)
                .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
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
        return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_ROW_MAPPER_ONE)
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(generic)
                .addException(SQLException.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(RowMapper.class), generic),
                        GeneralConstantUtility.EXTRACT_ARG_ROW_MAPPER)
                .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
                .addStatement(
                        "var list = "
                                + GeneralConstantUtility.EXTRACT_ROW_MAPPER
                                + "(rowMapper, rs)")
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

            var methodBuilder =
                    MethodSpec.methodBuilder(element.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addParameters(argument)
                            .returns(returnType);

            codeBlocks.forEach(methodBuilder::addCode);

            return methodBuilder.build();
        }
        log.error(
                "{} not instanceof {}. Impossible create method by element {}",
                Element.class,
                ExecutableElement.class,
                element);
        throw new ClassCastException(
                Element.class
                        + " not instanceof "
                        + ExecutableElement.class
                        + ". Impossible create method by element "
                        + element);
    }

    private List<CodeBlock> createCodeBlock(
            TypeMirror typeMirror, ExecutableElement executableElement) {
        var element = processingEnvironment.getTypeUtils().asElement(typeMirror);
        var packageOf = processingEnvironment.getElementUtils().getPackageOf(element);

        var canonicalName =
                packageOf.getQualifiedName().toString() + "." + element.getSimpleName().toString();

        if (canonicalName.equals(RowMapper.class.getCanonicalName())) {
            log.info("Create method for return {}", canonicalName);
            return createRowMapper(typeMirror);
        }
        log.info("Create method return extract object: {}", typeMirror);
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
                                                "No such generic by " + typeMirror));

        var fieldsName = CodeUtility.getFieldsName(generic, processingEnvironment);
        log.debug("Generic: {} fields from generic: {}", generic, fieldsName);
        var root = classTreeService.createTree(fieldsName, generic);
        log.debug("Tree from generic: {}", root);
        return List.of(
                generateRowMapper.generate(root),
                CodeBlock.builder()
                        .addStatement(
                                GeneralConstantUtility.RETURN_TEMPLATE
                                        + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER)
                        .build());
    }

    private List<CodeBlock> extractObject(TypeMirror typeMirror, String nameVariableResultSet) {
        var genericOptional = CodeUtility.getOneGeneric(typeMirror);

        var fieldsName =
                CodeUtility.getFieldsName(
                        genericOptional.orElse(typeMirror), processingEnvironment);
        log.debug("Type: {} fields from type: {}", genericOptional.orElse(typeMirror), fieldsName);
        var methodNameExtract =
                genericOptional.isPresent()
                        ? GeneralConstantUtility.EXTRACT_ROW_MAPPER
                        : GeneralConstantUtility.EXTRACT_ROW_MAPPER_ONE;

        var root = classTreeService.createTree(fieldsName, genericOptional.orElse(typeMirror));
        log.debug("Tree from type: {}", root);
        return List.of(
                generateRowMapper.generate(root),
                CodeBlock.builder()
                        .beginControlFlow("try")
                        .addStatement(
                                /*template string is "return %s(%s, %s)"*/
                                "return "
                                        + methodNameExtract
                                        + "("
                                        + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
                                        + ", "
                                        + nameVariableResultSet
                                        + ")")
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
