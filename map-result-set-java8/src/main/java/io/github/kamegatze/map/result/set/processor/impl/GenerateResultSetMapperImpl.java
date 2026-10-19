package io.github.kamegatze.map.result.set.processor.impl;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.Cursor;
import io.github.kamegatze.map.result.set.logger.Logger;
import io.github.kamegatze.map.result.set.logger.LoggerFactory;
import io.github.kamegatze.map.result.set.processor.ClassTree;
import io.github.kamegatze.map.result.set.processor.GenerateNewObject;
import io.github.kamegatze.map.result.set.processor.GenerateResultSetMapper;
import io.github.kamegatze.map.result.set.processor.ResultSetMapper;
import io.github.kamegatze.map.result.set.processor.exception.IllegalArgumentException;
import io.github.kamegatze.map.result.set.processor.utilities.CodeUtility;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

public final class GenerateResultSetMapperImpl implements GenerateResultSetMapper {

  private final Map<ElementKind, GenerateNewObject> generateNewObjectMap;

  private final Logger log;

  public GenerateResultSetMapperImpl(Map<ElementKind, GenerateNewObject> generateNewObjectMap) {
    this.generateNewObjectMap = generateNewObjectMap;
    log = LoggerFactory.create(GenerateResultSetMapperImpl.class);
  }

  @Override
  public CodeBlock generate(ClassTree root) {
    CodeBlock.Builder builder = CodeBlock.builder();
    Deque<ClassTree> stack = new ArrayDeque<>(root.children());

    builder.add(
        /*template is "$T %s = (%s, rowNum) ->{\n"*/
        "$T "
            + GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER
            + " = ("
            + GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE
            + root.uuid()
            + " , rowNum"
            + root.uuid()
            + ") -> {\n",
        ParameterizedTypeName.get(
            ClassName.get(ResultSetMapper.class), TypeName.get(root.typeMirror())));
    builder.indent();

    builder.add(
        createFields(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE + root.uuid()));

    while (!stack.isEmpty()) {
      if (stack.getLast().children().isEmpty()) {
        ClassTree item = stack.pollLast();

        TypeMirror typeMirror =
            CodeUtility.getOneGeneric(Objects.requireNonNull(item).typeMirror())
                .orElse(item.typeMirror());

        builder.add(
            /*template is $T %s = (%s, rowNum) ->{\n*/
            "$T "
                + item.name()
                + item.parent().uuid()
                + " = ("
                + GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE
                + item.uuid()
                + " , rowNum1"
                + item.uuid()
                + ") -> {\n",
            ParameterizedTypeName.get(
                ClassName.get(ResultSetMapper.class), TypeName.get(typeMirror)));
        builder.indent();

        builder.add(
            createFields(
                item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE + item.uuid()));

        builder.add(
            createNewObject(
                item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE + item.uuid()));

        builder.addStatement(GeneralConstantUtility.RETURN_TEMPLATE + item.name() + item.uuid());
        builder.unindent();
        builder.add("};\n");

        item.parent().children().remove(item);
      } else {
        stack.addAll(stack.getLast().children());
      }
    }

    builder.add(
        createNewObject(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE + root.uuid()));
    builder.addStatement(GeneralConstantUtility.RETURN_TEMPLATE + root.name() + root.uuid());
    builder.unindent();
    builder.add("};\n");
    return builder.build();
  }

  private CodeBlock createFields(ClassTree item, String nameResultSet) {
    CodeBlock.Builder builder = CodeBlock.builder();
    item.fields().stream()
        .filter(it -> Objects.isNull(it.getAnnotation(Cursor.class)))
        .forEach(
            it -> {
              ConventionFieldPolicy.Policy policy =
                  CodeUtility.getColumnPolicy(it, item.typeMirror());
              log.info(
                  "column policy for type: {0} is policy: {1} is field: {2}",
                  item.typeMirror(), policy, it.getSimpleName());
              builder.addStatement(
                  /*template is "var %s = %s.getObject($S, $T.class)"*/
                  "$T " + it + item.uuid() + " = " + nameResultSet + ".getObject($S, $T.class)",
                  TypeName.get(it.asType()),
                  CodeUtility.getColumnName(it, policy),
                  TypeName.get(it.asType()));
            });

    return builder.build();
  }

  private CodeBlock createNewObject(ClassTree item, String resultSetName) {
    TypeMirror typeMirror = CodeUtility.getOneGeneric(item.typeMirror()).orElse(item.typeMirror());
    Element element = CodeUtility.getElementFromTypeMirror(typeMirror);
    ElementKind elementKind = element.getKind();
    GenerateNewObject generateNewObject = generateNewObjectMap.get(elementKind);
    if (Objects.isNull(generateNewObject)) {
      throw new IllegalArgumentException(
          String.format(
              "Not found support %s because support only %s",
              ElementKind.class, generateNewObjectMap.keySet()),
          element);
    }
    return generateNewObject.createNew(item, typeMirror, resultSetName);
  }
}
