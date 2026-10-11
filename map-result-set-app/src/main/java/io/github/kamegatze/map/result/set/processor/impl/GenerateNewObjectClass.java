package io.github.kamegatze.map.result.set.processor.impl;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.Cursor;
import io.github.kamegatze.map.result.set.processor.ClassTree;
import io.github.kamegatze.map.result.set.processor.GenerateNewObject;
import io.github.kamegatze.map.result.set.processor.utilities.CodeUtility;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

public final class GenerateNewObjectClass implements GenerateNewObject {
  @Override
  public CodeBlock createNew(ClassTree item, TypeMirror typeMirror, String resultSetName) {
    CodeBlock.Builder builder = CodeBlock.builder();
    TypeName typeName = TypeName.get(typeMirror);
    builder.addStatement("$T " + item.name() + item.uuid() + " = new $T()", typeName, typeName);
    item.fields()
        .forEach(
            it -> {
              ConventionFieldPolicy.Policy policy =
                  CodeUtility.getColumnPolicy(it, item.typeMirror());
              if (Objects.isNull(it.getAnnotation(Cursor.class))) {
                builder.addStatement(
                    /*template is "%s.%s(%s)"*/
                    item.name()
                        + item.uuid()
                        + "."
                        + CodeUtility.generateSetMethodName(it.toString())
                        + "("
                        + it.getSimpleName()
                        + item.uuid()
                        + ")");
                return;
              }
              Optional<TypeMirror> genericOption = CodeUtility.getOneGeneric(it.asType());
              StringBuilder prefixBuilder =
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
                        .append(it.getSimpleName())
                        .append(item.uuid())
                        .append(",($T) ")
                        .append(resultSetName)
                        .append(".getObject($S)))")
                        .toString(),
                    ResultSet.class,
                    CodeUtility.getColumnName(it, policy));
                return;
              }
              builder.addStatement(
                  /*template is "%s.%s(%s(%s, ($T) %s.getObject($S)))"*/
                  prefixBuilder
                      .append(GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE)
                      .append("(")
                      .append(it.getSimpleName())
                      .append(item.uuid())
                      .append(",")
                      .append("($T) ")
                      .append(resultSetName)
                      .append(".getObject($S)))")
                      .toString(),
                  ResultSet.class,
                  CodeUtility.getColumnName(it, policy));
            });
    return builder.build();
  }

  @Override
  public ElementKind type() {
    return ElementKind.CLASS;
  }
}
