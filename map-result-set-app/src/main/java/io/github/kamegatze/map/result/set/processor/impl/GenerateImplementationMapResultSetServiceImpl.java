package io.github.kamegatze.map.result.set.processor.impl;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.github.kamegatze.map.result.set.logger.Logger;
import io.github.kamegatze.map.result.set.logger.LoggerFactory;
import io.github.kamegatze.map.result.set.processor.ClassTree;
import io.github.kamegatze.map.result.set.processor.ClassTreeService;
import io.github.kamegatze.map.result.set.processor.GenerateImplementationMapResultSetService;
import io.github.kamegatze.map.result.set.processor.GenerateResultSetMapper;
import io.github.kamegatze.map.result.set.processor.ResultSetMapper;
import io.github.kamegatze.map.result.set.processor.exception.ClassCastException;
import io.github.kamegatze.map.result.set.processor.exception.ExtractResultSetException;
import io.github.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import io.github.kamegatze.map.result.set.processor.exception.NoSuchElementException;
import io.github.kamegatze.map.result.set.processor.exception.UnsupportedTypeException;
import io.github.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import io.github.kamegatze.map.result.set.processor.utilities.CodeUtility;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public final class GenerateImplementationMapResultSetServiceImpl
    implements GenerateImplementationMapResultSetService {

  private final ProcessingEnvironment processingEnvironment;
  private final GenerateResultSetMapper generateResultSetMapper;
  private final ClassTreeService classTreeService;

  private final Logger log;

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

    List<MethodSpec> methodSpecs =
        element.getEnclosedElements().stream()
            .filter(it -> it.getKind().equals(ElementKind.METHOD))
            .filter(it -> !it.getModifiers().contains(Modifier.DEFAULT))
            .map(
                it -> {
                  String signatureOfMethod = it.getSimpleName() + it.asType().toString();

                  log.info("Begin operation create method: {0}", signatureOfMethod);
                  MethodSpec method = createMethod(it);
                  log.info("End operation create method: {0}", signatureOfMethod);
                  return method;
                })
            .collect(Collectors.toList());

    TypeSpec typeSpec =
        TypeSpec.classBuilder(
                element.getSimpleName() + GeneralConstantUtility.POSTFIX_IMPLEMENT_INTERFACE)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(element.asType())
            .addMethods(methodSpecs)
            .addMethod(createExtractMethod())
            .addMethod(createExtractMethodOne())
            .build();
    PackageElement packageElement = processingEnvironment.getElementUtils().getPackageOf(element);

    return JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec).build();
  }

  @Override
  public void write(JavaFile javaFile, Element element) {
    try {
      javaFile.writeTo(processingEnvironment.getFiler());
    } catch (IOException e) {
      throw new WriteJavaFileException(
          "Error write java file with error message: " + e.getMessage(), element, e);
    }
  }

  private MethodSpec createExtractMethod() {
    TypeVariableName generic = TypeVariableName.get("T");
    return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER)
        .addModifiers(Modifier.PRIVATE)
        .addTypeVariable(generic)
        .addException(SQLException.class)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get(ResultSetMapper.class), generic),
            GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER)
        .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
        .addStatement(
            "$T list = new $T()",
            ParameterizedTypeName.get(ClassName.get(List.class), generic),
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
    TypeVariableName generic = TypeVariableName.get("T");
    return MethodSpec.methodBuilder(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE)
        .addModifiers(Modifier.PRIVATE)
        .addTypeVariable(generic)
        .addException(SQLException.class)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get(ResultSetMapper.class), generic),
            GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER)
        .addParameter(ResultSet.class, GeneralConstantUtility.EXTRACT_ARG_RESULT_SET)
        .addStatement(
            "$T list = "
                + GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER
                + "("
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET_MAPPER
                + ", "
                + GeneralConstantUtility.EXTRACT_ARG_RESULT_SET
                + ")",
            ParameterizedTypeName.get(ClassName.get(List.class), generic))
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
    if (element instanceof ExecutableElement) {
      ExecutableElement executableElement = (ExecutableElement) element;
      TypeName returnType = TypeName.get(executableElement.getReturnType());

      List<CodeBlock> codeBlocks =
          createCodeBlock(executableElement.getReturnType(), executableElement);

      List<ParameterSpec> argument =
          executableElement.getParameters().stream()
              .map(ParameterSpec::get)
              .collect(Collectors.toList());

      MethodSpec.Builder methodBuilder =
          MethodSpec.methodBuilder(element.getSimpleName().toString())
              .addModifiers(Modifier.PUBLIC)
              .addAnnotation(Override.class)
              .addParameters(argument)
              .returns(returnType);

      codeBlocks.forEach(methodBuilder::addCode);

      return methodBuilder.build();
    }

    String msg =
        String.format(
            "%s not instanceof %s. Impossible create method by element %s",
            Element.class, ExecutableElement.class, element);

    throw new ClassCastException(msg, element);
  }

  private List<CodeBlock> createCodeBlock(
      TypeMirror typeMirror, ExecutableElement executableElement) {
    Element element = processingEnvironment.getTypeUtils().asElement(typeMirror);
    PackageElement packageOf = processingEnvironment.getElementUtils().getPackageOf(element);

    String canonicalName = packageOf.getQualifiedName() + "." + element.getSimpleName();

    if (canonicalName.equals(ResultSetMapper.class.getCanonicalName())) {
      log.info("Create method for return {0}", canonicalName);
      return createResultSetMapper(typeMirror);
    }
    log.info("Create method return extract object: {0}", typeMirror);
    return extractObject(
        typeMirror, variableName(executableElement, ResultSet.class.getCanonicalName()));
  }

  private String variableName(ExecutableElement executableElement, String canonicalNameClass) {
    return executableElement.getParameters().stream()
        .filter(
            it -> {
              Element element = processingEnvironment.getTypeUtils().asElement(it.asType());
              PackageElement packageOf =
                  processingEnvironment.getElementUtils().getPackageOf(element);
              String canonicalName = packageOf.getQualifiedName() + "." + element.getSimpleName();
              return canonicalName.equals(canonicalNameClass);
            })
        .map(it -> it.getSimpleName().toString())
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("No such variable name for ResultSet"));
  }

  private List<CodeBlock> createResultSetMapper(TypeMirror typeMirror) {
    TypeMirror generic =
        CodeUtility.getOneGeneric(typeMirror)
            .orElseThrow(() -> new NoSuchElementException("No such generic by " + typeMirror));

    List<VariableElement> fieldsName = CodeUtility.getFieldsName(generic, processingEnvironment);
    log.debug("Generic: {0} fields from generic: {1}", generic, fieldsName);
    ClassTree root = classTreeService.createTree(fieldsName, generic);
    log.debug("Tree from generic: {0}", root);
    List<CodeBlock> codeBlocks = new ArrayList<>();
    codeBlocks.add(generateResultSetMapper.generate(root));
    codeBlocks.add(
        CodeBlock.builder()
            .addStatement(
                GeneralConstantUtility.RETURN_TEMPLATE
                    + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER)
            .build());
    return Collections.unmodifiableList(codeBlocks);
  }

  private List<CodeBlock> extractObject(TypeMirror typeMirror, String nameVariableResultSet) {
    Optional<TypeMirror> genericOptional = CodeUtility.getOneGeneric(typeMirror);

    List<VariableElement> fieldsName =
        CodeUtility.getFieldsName(genericOptional.orElse(typeMirror), processingEnvironment);
    log.info("Type: {0} fields from type: {1}", genericOptional.orElse(typeMirror), fieldsName);

    ClassTree root = classTreeService.createTree(fieldsName, genericOptional.orElse(typeMirror));
    log.debug("Tree from type: {0}", root);
    List<CodeBlock> codeBlocks = new ArrayList<>();
    codeBlocks.add(generateResultSetMapper.generate(root));
    codeBlocks.add(
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
    return Collections.unmodifiableList(codeBlocks);
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
    String clazz = typeMirror.toString().replaceFirst("<[a-zA-Z.]*>", "");

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

    throw new UnsupportedTypeException(
        "Current type " + typeMirror + "is not supported",
        CodeUtility.getElementFromTypeMirror(typeMirror));
  }
}
