package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.logger.LoggerFactory;
import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import com.kamegatze.map.result.set.processor.GenerateResultSetMapper;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.exception.ExtractResultSetException;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import com.kamegatze.map.result.set.processor.exception.UnsupportedTypeException;
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
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public final class GenerateImplementationMapResultSetServiceImpl
    implements GenerateImplementationMapResultSetService {

  private final ProcessingEnvironment processingEnvironment;
  private final GenerateResultSetMapper generateResultSetMapper;
  private final ClassTreeService classTreeService;

  private final System.Logger log;

  public GenerateImplementationMapResultSetServiceImpl(
      ProcessingEnvironment processingEnvironment,
      GenerateResultSetMapper generateResultSetMapper,
      ClassTreeService classTreeService) {
    this.processingEnvironment = processingEnvironment;
    this.generateResultSetMapper = generateResultSetMapper;
    this.classTreeService = classTreeService;
    log = LoggerFactory.create(GenerateImplementationMapResultSetServiceImpl.class);
  }

  @Override
  public JavaFile generate(Element element) {

    var methodSpecs =
        element.getEnclosedElements().stream()
            .filter(it -> it.getKind().equals(ElementKind.METHOD))
            .filter(it -> !it.getModifiers().contains(Modifier.DEFAULT))
            .map(
                it -> {
                  var signatureOfMethod = it.getSimpleName() + it.asType().toString();

                  log.log(
                      System.Logger.Level.INFO,
                      "Begin operation create method: {0}",
                      signatureOfMethod);
                  var method = createMethod(it);
                  log.log(
                      System.Logger.Level.INFO,
                      "End operation create method: {0}",
                      signatureOfMethod);
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
    return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER)
        .addModifiers(Modifier.PRIVATE)
        .addTypeVariable(generic)
        .addException(SQLException.class)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get(ResultSetMapper.class), generic),
            GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER)
        .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
        .addStatement(
            "var list = new $T()",
            ParameterizedTypeName.get(ClassName.get(ArrayList.class), generic))
        .beginControlFlow("while(rs.next())")
        .addStatement(
            "list.add("
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER
                + ".mapRow("
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET
                + ", "
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET
                + ".getRow()))")
        .endControlFlow()
        .addStatement("return list")
        .returns(ParameterizedTypeName.get(ClassName.get(List.class), generic))
        .build();
  }

  private MethodSpec createExtractMethodOne() {
    var generic = TypeVariableName.get("T");
    return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE)
        .addModifiers(Modifier.PRIVATE)
        .addTypeVariable(generic)
        .addException(SQLException.class)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get(ResultSetMapper.class), generic),
            GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER)
        .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
        .addStatement(
            "var list = "
                + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER
                + "("
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER
                + ", "
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET
                + ")")
        .beginControlFlow("if(list.isEmpty())")
        .addStatement("return $T.empty()", ClassName.get(Optional.class))
        .endControlFlow()
        .beginControlFlow("if(list.size() > 1)")
        .addStatement(
            "throw new $T($S)",
            ClassName.get(MoreThenOneItemException.class),
            "ResultSet more then one item")
        .endControlFlow()
        .addStatement("return $T.ofNullable(list.get(0))", ClassName.get(Optional.class))
        .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), generic))
        .build();
  }

  private MethodSpec createMethod(Element element) {
    if (element instanceof ExecutableElement executableElement) {
      var returnType = TypeName.get(executableElement.getReturnType());

      var codeBlocks = createCodeBlock(executableElement.getReturnType(), executableElement);

      var argument = executableElement.getParameters().stream().map(ParameterSpec::get).toList();

      var methodBuilder =
          MethodSpec.methodBuilder(element.getSimpleName().toString())
              .addModifiers(Modifier.PUBLIC)
              .addAnnotation(Override.class)
              .addParameters(argument)
              .returns(returnType);

      codeBlocks.forEach(methodBuilder::addCode);

      return methodBuilder.build();
    }

    var msg =
        "%s not instanceof %s. Impossible create method by element %s"
            .formatted(Element.class, ExecutableElement.class, element);

    log.log(System.Logger.Level.ERROR, msg);
    throw new ClassCastException(msg);
  }

  private List<CodeBlock> createCodeBlock(
      TypeMirror typeMirror, ExecutableElement executableElement) {
    var element = processingEnvironment.getTypeUtils().asElement(typeMirror);
    var packageOf = processingEnvironment.getElementUtils().getPackageOf(element);

    var canonicalName =
        packageOf.getQualifiedName().toString() + "." + element.getSimpleName().toString();

    if (canonicalName.equals(ResultSetMapper.class.getCanonicalName())) {
      log.log(System.Logger.Level.INFO, "Create method for return {0}", canonicalName);
      return createResultSetMapper(typeMirror);
    }
    log.log(System.Logger.Level.INFO, "Create method return extract object: {0}", typeMirror);
    return extractObject(
        typeMirror, variableName(executableElement, ResultSet.class.getCanonicalName()));
  }

  private String variableName(ExecutableElement executableElement, String canonicalNameClass) {
    return executableElement.getParameters().stream()
        .filter(
            it -> {
              var element = processingEnvironment.getTypeUtils().asElement(it.asType());
              var packageOf = processingEnvironment.getElementUtils().getPackageOf(element);
              var canonicalName =
                  packageOf.getQualifiedName().toString()
                      + "."
                      + element.getSimpleName().toString();
              return canonicalName.equals(canonicalNameClass);
            })
        .map(it -> it.getSimpleName().toString())
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("No such variable name for ResultSet"));
  }

  private List<CodeBlock> createResultSetMapper(TypeMirror typeMirror) {
    var generic =
        CodeUtility.getOneGeneric(typeMirror)
            .orElseThrow(() -> new NoSuchElementException("No such generic by " + typeMirror));

    var fieldsName = CodeUtility.getFieldsName(generic, processingEnvironment);
    log.log(
        System.Logger.Level.DEBUG, "Generic: {0} fields from generic: {1}", generic, fieldsName);
    var root = classTreeService.createTree(fieldsName, generic);
    log.log(System.Logger.Level.DEBUG, "Tree from generic: {0}", root);
    return List.of(
        generateResultSetMapper.generate(root),
        CodeBlock.builder()
            .addStatement(
                GeneralConstantUtility.RETURN_TEMPLATE
                    + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER)
            .build());
  }

  private List<CodeBlock> extractObject(TypeMirror typeMirror, String nameVariableResultSet) {
    var genericOptional = CodeUtility.getOneGeneric(typeMirror);

    var fieldsName =
        CodeUtility.getFieldsName(genericOptional.orElse(typeMirror), processingEnvironment);
    log.log(
        System.Logger.Level.INFO,
        "Type: {0} fields from type: {1}",
        genericOptional.orElse(typeMirror),
        fieldsName);

    var root = classTreeService.createTree(fieldsName, genericOptional.orElse(typeMirror));
    log.log(System.Logger.Level.DEBUG, "Tree from type: {0}", root);
    return List.of(
        generateResultSetMapper.generate(root),
        CodeBlock.builder()
            .beginControlFlow("try")
            .addStatement(
                extractToType(typeMirror, nameVariableResultSet, genericOptional.isPresent()))
            .endControlFlow()
            .beginControlFlow("catch($T e)", SQLException.class)
            .addStatement(
                "throw new $T($S, e)",
                ExtractResultSetException.class,
                "Error extract from ResultSet")
            .endControlFlow()
            .build());
  }

  private CodeBlock extractToType(
      TypeMirror typeMirror, String nameVariableResultSet, boolean isGenericType) {
    if (!isGenericType) {
      return CodeBlock.builder()
          .add(
              "return "
                  + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE
                  + "("
                  + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
                  + ", "
                  + nameVariableResultSet
                  + ").orElse(null)")
          .build();
    }
    var clazz = typeMirror.toString().replaceFirst("<[a-zA-Z.]*>", "");

    if (clazz.equals(Iterable.class.getCanonicalName())
        || clazz.equals(Collection.class.getCanonicalName())
        || clazz.equals(List.class.getCanonicalName())) {
      return CodeBlock.builder()
          .add(
              "return $T.copyOf("
                  + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER
                  + "("
                  + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
                  + ", "
                  + nameVariableResultSet
                  + "))",
              List.class)
          .build();
    }

    if (clazz.equals(Set.class.getCanonicalName())) {
      return CodeBlock.builder()
          .add(
              "return $T.copyOf("
                  + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER
                  + "("
                  + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
                  + ", "
                  + nameVariableResultSet
                  + "))",
              Set.class)
          .build();
    }

    if (clazz.equals(Optional.class.getCanonicalName())) {
      return CodeBlock.builder()
          .add(
              "return "
                  + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE
                  + "("
                  + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
                  + ", "
                  + nameVariableResultSet
                  + ")")
          .build();
    }

    throw new UnsupportedTypeException("Current type " + typeMirror + "is not supported");
  }
}
