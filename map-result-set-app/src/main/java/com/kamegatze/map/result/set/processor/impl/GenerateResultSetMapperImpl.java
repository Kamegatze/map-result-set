package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.Cursor;
import com.kamegatze.map.result.set.processor.ClassTree;
import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.GenerateResultSetMapper;
import com.kamegatze.map.result.set.processor.ResultSetMapper;
import com.kamegatze.map.result.set.processor.utilities.CodeUtility;
import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

public record GenerateResultSetMapperImpl(
    ProcessingEnvironment processingEnvironment, ClassTreeService classTreeService)
    implements GenerateResultSetMapper {

  @Override
  public CodeBlock generate(ClassTree root) {
    var builder = CodeBlock.builder();
    var stack = new ArrayDeque<>(root.children());

    builder.add(
        /*template is "$T %s = (%s, rowNum) ->{\n"*/
        "$T "
            + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
            + " = ("
            + GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE
            + " , rowNum) -> {\n",
        ParameterizedTypeName.get(
            ClassName.get(ResultSetMapper.class), TypeName.get(root.typeMirror())));
    builder.indent();

    builder.add(createFields(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE));

    while (!stack.isEmpty()) {
      if (stack.getLast().children().isEmpty()) {
        var item = stack.pollLast();

        var typeMirror =
            CodeUtility.getOneGeneric(Objects.requireNonNull(item).typeMirror())
                .orElse(item.typeMirror());

        builder.add(
            /*template is $T %s = (%s, rowNum) ->{\n*/
            "$T "
                + item.name()
                + item.parent().uuid()
                + " = ("
                + GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE
                + " , rowNum1) -> {\n",
            ParameterizedTypeName.get(
                ClassName.get(ResultSetMapper.class), TypeName.get(typeMirror)));
        builder.indent();

        builder.add(createFields(item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE));

        builder.add(createNewObject(item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE));

        builder.addStatement(GeneralConstantUtility.RETURN_TEMPLATE + item.name() + item.uuid());
        builder.unindent();
        builder.add("};\n");

        item.parent().children().remove(item);
      } else {
        stack.addAll(stack.getLast().children());
      }
    }

    builder.add(createNewObject(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE));

    builder.addStatement(GeneralConstantUtility.RETURN_TEMPLATE + root.name() + root.uuid());
    builder.unindent();
    builder.add("};\n");
    return builder.build();
  }

  private CodeBlock createFields(ClassTree item, String nameResultSet) {
    var builder = CodeBlock.builder();
    item.fields().stream()
        .filter(it -> Objects.isNull(it.getAnnotation(Cursor.class)))
        .forEach(
            it ->
                builder.addStatement(
                    /*template is "var %s = %s.getObject($S, $T.class)"*/
                    "var " + it + item.uuid() + " = " + nameResultSet + ".getObject($S, $T.class)",
                    CodeUtility.getColumnName(it),
                    TypeName.get(it.asType())));
    return builder.build();
  }

  private CodeBlock createNewObject(ClassTree item, String resultSetName) {
    var typeMirror = CodeUtility.getOneGeneric(item.typeMirror()).orElse(item.typeMirror());
    if (ElementKind.RECORD.equals(
        processingEnvironment.getTypeUtils().asElement(typeMirror).getKind())) {
      return createNewRecord(item, typeMirror, resultSetName);
    }
    return createNewClass(item, typeMirror, resultSetName);
  }

  private CodeBlock createNewClass(ClassTree item, TypeMirror typeMirror, String resultSetName) {
    var builder = CodeBlock.builder();
    builder.addStatement(
        "var " + item.name() + item.uuid() + " = new $T()", TypeName.get(typeMirror));
    item.fields()
        .forEach(
            it -> {
              if (Objects.isNull(it.getAnnotation(Cursor.class))) {
                builder.addStatement(
                    /*template is "%s.%s(%s)"*/
                    item.name()
                        + item.uuid()
                        + "."
                        + CodeUtility.generateSetMethodName(it.toString())
                        + "("
                        + it.getSimpleName().toString()
                        + item.uuid()
                        + ")");
                return;
              }
              var genericOption = CodeUtility.getOneGeneric(it.asType());
              var prefixBuilder =
                  new StringBuilder()
                      .append(item.name())
                      .append(item.uuid())
                      .append(".")
                      .append(CodeUtility.generateSetMethodName(it.toString()))
                      .append("(");

              if (genericOption.isPresent()) {
                builder.addStatement(
                    /*template is "%s.%s(%s(%s, ($T) %s.getObject($S)))"*/
                    prefixBuilder
                        .append(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER)
                        .append("(")
                        .append(it.getSimpleName().toString())
                        .append(item.uuid())
                        .append(",($T) ")
                        .append(resultSetName)
                        .append(".getObject($S)))")
                        .toString(),
                    ResultSet.class,
                    CodeUtility.getColumnName(it));
                return;
              }
              builder.addStatement(
                  /*template is "%s.%s(%s(%s, ($T) %s.getObject($S)))"*/
                  prefixBuilder
                      .append(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE)
                      .append("(")
                      .append(it.getSimpleName().toString())
                      .append(item.uuid())
                      .append(",")
                      .append("($T) ")
                      .append(resultSetName)
                      .append(".getObject($S)))")
                      .toString(),
                  ResultSet.class,
                  CodeUtility.getColumnName(it));
            });
    return builder.build();
  }

  private CodeBlock createNewRecord(ClassTree item, TypeMirror typeMirror, String resultSetName) {
    var builder = CodeBlock.builder();
    builder.add("var " + item.name() + item.uuid() + " = new $T(\n", TypeName.get(typeMirror));
    IntStream.range(0, item.fields().size())
        .forEach(
            index -> {
              var postfix = index == item.fields().size() - 1 ? ");" : ",";
              var extractPostfix =
                  "("
                      + item.fields().get(index).getSimpleName().toString()
                      + item.uuid()
                      + ", ($T) "
                      + resultSetName
                      + ".getObject($S))"
                      + postfix
                      + "\n";

              var genericOption = CodeUtility.getOneGeneric(item.fields().get(index).asType());

              if (Objects.isNull(item.fields().get(index).getAnnotation(Cursor.class))) {
                builder.add(
                    item.fields().get(index).getSimpleName().toString()
                        + item.uuid()
                        + postfix
                        + "\n");
                return;
              }
              if (genericOption.isPresent()) {
                builder.add(
                    /*template is "%s(%s, ($T) %s.getObject($S))%s\n"*/
                    GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER + extractPostfix,
                    ResultSet.class,
                    CodeUtility.getColumnName(item.fields().get(index)));
                return;
              }
              builder.add(
                  /*template is "%s(%s, ($T) %s.getObject($S))%s\n"*/
                  GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE + extractPostfix,
                  ResultSet.class,
                  CodeUtility.getColumnName(item.fields().get(index)));
            });
    return builder.build();
  }
}
