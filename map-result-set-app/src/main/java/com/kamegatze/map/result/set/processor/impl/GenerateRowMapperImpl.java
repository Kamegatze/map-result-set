package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.Cursor;
import com.kamegatze.map.result.set.processor.ClassTree;
import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.GenerateRowMapper;
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
import org.springframework.jdbc.core.RowMapper;

public record GenerateRowMapperImpl(
        ProcessingEnvironment processingEnvironment, ClassTreeService classTreeService)
        implements GenerateRowMapper {

    @Override
    public CodeBlock generate(ClassTree root) {
        var builder = CodeBlock.builder();
        var stack = new ArrayDeque<>(root.children());

        builder.add(
                "$T %s = (%s, rowNum) -> {"
                                .formatted(
                                        GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER,
                                        GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE)
                        + "\n",
                ParameterizedTypeName.get(
                        ClassName.get(RowMapper.class), TypeName.get(root.typeMirror())));
        builder.indent();

        builder.add(createFields(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE));

        while (!stack.isEmpty()) {
            if (stack.getLast().children().isEmpty()) {
                var item = stack.pollLast();

                var typeMirror =
                        CodeUtility.getOneGeneric(Objects.requireNonNull(item).typeMirror())
                                .orElse(item.typeMirror());

                builder.add(
                        "$T %s = (%s, rowNum1) -> {"
                                        .formatted(
                                                item.name() + item.parent().uuid(),
                                                GeneralConstantUtility
                                                        .VARIABLE_RESULT_SET_TWO_ENSURE)
                                + "\n",
                        ParameterizedTypeName.get(
                                ClassName.get(RowMapper.class), TypeName.get(typeMirror)));
                builder.indent();

                builder.add(
                        createFields(item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE));

                builder.add(
                        createNewObject(
                                item, GeneralConstantUtility.VARIABLE_RESULT_SET_TWO_ENSURE));

                builder.addStatement(
                        GeneralConstantUtility.RETURN_TEMPLATE.formatted(
                                item.name() + item.uuid()));
                builder.unindent();
                builder.add("};\n");

                item.parent().children().remove(item);
            } else {
                stack.addAll(stack.getLast().children());
            }
        }

        builder.add(createNewObject(root, GeneralConstantUtility.VARIABLE_RESULT_SET_ONE_ENSURE));

        builder.addStatement(
                GeneralConstantUtility.RETURN_TEMPLATE.formatted(root.name() + root.uuid()));
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
                                        "var %s = %s.getObject($S, $T.class)"
                                                .formatted(it + item.uuid(), nameResultSet),
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
                "var %s = new $T()".formatted(item.name() + item.uuid()), TypeName.get(typeMirror));
        item.fields()
                .forEach(
                        it -> {
                            if (Objects.isNull(it.getAnnotation(Cursor.class))) {
                                builder.addStatement(
                                        "%s.%s(%s)"
                                                .formatted(
                                                        item.name() + item.uuid(),
                                                        CodeUtility.generateSetMethodName(
                                                                it.toString()),
                                                        it.getSimpleName().toString()
                                                                + item.uuid()));
                                return;
                            }
                            var genericOption = CodeUtility.getOneGeneric(it.asType());
                            var template = "%s.%s(%s(%s, ($T) %s.getObject($S)))";

                            if (genericOption.isPresent()) {
                                builder.addStatement(
                                        template.formatted(
                                                item.name() + item.uuid(),
                                                CodeUtility.generateSetMethodName(it.toString()),
                                                GeneralConstantUtility.EXTRACT_ROW_MAPPER,
                                                it.getSimpleName().toString() + item.uuid(),
                                                resultSetName),
                                        ResultSet.class,
                                        CodeUtility.getColumnName(it));
                                return;
                            }
                            builder.addStatement(
                                    template.formatted(
                                            item.name() + item.uuid(),
                                            CodeUtility.generateSetMethodName(it.toString()),
                                            GeneralConstantUtility.EXTRACT_ROW_MAPPER_ONE,
                                            it.getSimpleName().toString() + item.uuid(),
                                            resultSetName),
                                    ResultSet.class,
                                    CodeUtility.getColumnName(it));
                        });
        return builder.build();
    }

    private CodeBlock createNewRecord(ClassTree item, TypeMirror typeMirror, String resultSetName) {
        var builder = CodeBlock.builder();
        builder.add(
                "var %s = new $T(".formatted(item.name() + item.uuid()) + "\n",
                TypeName.get(typeMirror));
        IntStream.range(0, item.fields().size())
                .forEach(
                        index -> {
                            var templateExtract = "%s(%s, ($T) %s.getObject($S))%s";
                            var template = "%s%s";
                            var postfix = index == item.fields().size() - 1 ? ");" : ",";
                            var genericOption =
                                    CodeUtility.getOneGeneric(item.fields().get(index).asType());

                            if (Objects.isNull(
                                    item.fields().get(index).getAnnotation(Cursor.class))) {
                                builder.add(
                                        template.formatted(
                                                        item.fields()
                                                                        .get(index)
                                                                        .getSimpleName()
                                                                        .toString()
                                                                + item.uuid(),
                                                        postfix)
                                                + "\n");
                                return;
                            }
                            if (genericOption.isPresent()) {
                                builder.add(
                                        templateExtract.formatted(
                                                        GeneralConstantUtility.EXTRACT_ROW_MAPPER,
                                                        item.fields()
                                                                        .get(index)
                                                                        .getSimpleName()
                                                                        .toString()
                                                                + item.uuid(),
                                                        resultSetName,
                                                        postfix)
                                                + "\n",
                                        ResultSet.class,
                                        CodeUtility.getColumnName(item.fields().get(index)));
                                return;
                            }
                            builder.add(
                                    templateExtract.formatted(
                                                    GeneralConstantUtility.EXTRACT_ROW_MAPPER_ONE,
                                                    item.fields()
                                                                    .get(index)
                                                                    .getSimpleName()
                                                                    .toString()
                                                            + item.uuid(),
                                                    resultSetName,
                                                    postfix)
                                            + "\n",
                                    ResultSet.class,
                                    CodeUtility.getColumnName(item.fields().get(index)));
                        });
        return builder.build();
    }
}
