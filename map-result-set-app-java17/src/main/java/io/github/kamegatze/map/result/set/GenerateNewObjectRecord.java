package io.github.kamegatze.map.result.set;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import io.github.kamegatze.map.result.set.processor.ClassTree;
import io.github.kamegatze.map.result.set.processor.GenerateNewObject;
import io.github.kamegatze.map.result.set.processor.utilities.CodeUtility;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.stream.IntStream;

public final class GenerateNewObjectRecord implements GenerateNewObject {
  @Override
  public CodeBlock createNew(ClassTree item, TypeMirror typeMirror, String resultSetName) {
    var builder = CodeBlock.builder();
    var typeName = TypeName.get(typeMirror);
    builder.add("$T " + item.name() + item.uuid() + " = new $T(\n", typeName, typeName);
    IntStream.range(0, item.fields().size())
        .forEach(
            index -> {
              var policy = CodeUtility.getColumnPolicy(item.fields().get(index), item.typeMirror());

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
                    CodeUtility.getColumnName(item.fields().get(index), policy));
                return;
              }
              builder.add(

                  /*template is "%s(%s, ($T) %s.getObject($S))%s\n"*/

                  GeneralConstantUtility.EXTRACT_RESULT_SET_MAPPER_ONE + extractPostfix,
                  ResultSet.class,
                  CodeUtility.getColumnName(item.fields().get(index), policy));
            });
    return builder.build();
  }

  @Override
  public ElementKind type() {
    return ElementKind.RECORD;
  }
}
