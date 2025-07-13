package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.Column;
import com.kamegatze.map.result.set.Cursor;
import com.kamegatze.map.result.set.processor.ProcessorAnnotation;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import com.kamegatze.map.result.set.processor.exception.WriteJavaFileException;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.TypeVariableName;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.springframework.jdbc.core.RowMapper;

public final class ProcessorAnnotationImpl implements ProcessorAnnotation {

    private final ProcessingEnvironment processingEnvironment;

    private static final String EXTRACT_ROW_MAPPER = "extractRowMapper";
    private static final String EXTRACT_ROW_MAPPER_ONE = "extractRowMapperOne";

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
        return MethodSpec.methodBuilder(EXTRACT_ROW_MAPPER)
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
        return MethodSpec.methodBuilder(EXTRACT_ROW_MAPPER_ONE)
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(generic)
                .addException(SQLException.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(RowMapper.class), generic),
                        "rowMapper")
                .addParameter(ResultSet.class, "rs")
                .addStatement("var list = extractRowMapper(rowMapper, rs)")
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

            var codeBlock = createCodeBlock(executableElement.getReturnType());

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

    private CodeBlock createCodeBlock(TypeMirror typeMirror) {
        var element = processingEnvironment.getTypeUtils().asElement(typeMirror);
        var packageOf = processingEnvironment.getElementUtils().getPackageOf(element);

        var canonicalName =
                packageOf.getQualifiedName().toString() + "." + element.getSimpleName().toString();

        if (canonicalName.equals(RowMapper.class.getCanonicalName())) {
            return createRowMapper(typeMirror);
        }
        return extractObject();
    }

    private CodeBlock createRowMapper(TypeMirror typeMirror) {
        var generic =
                getOneGeneric(typeMirror)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No such generic by %s"
                                                        .formatted(typeMirror.toString())));

        var fieldsName = getFieldsName(generic);

        var root = createTree(fieldsName, generic);
        return createRowMapper(root);
    }

    private CodeBlock extractObject() {
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
        var cursor = variableElement.getAnnotation(Cursor.class);
        if (Objects.nonNull(cursor)) {
            return cursor.value();
        }
        return variableElement.getSimpleName().toString();
    }

    private ClassTree createTree(List<VariableElement> variableElements, TypeMirror rootType) {
        var uuidRoot = UUID.randomUUID().toString().replace("-", "");
        var root =
                new ClassTree(
                        "root", uuidRoot, rootType, null, new ArrayList<>(), variableElements);

        var queue = new ArrayDeque<ClassTree>();
        queue.add(root);

        while (!queue.isEmpty()) {
            var item = queue.pollFirst();

            item.fields().stream()
                    .filter(it -> Objects.nonNull(it.getAnnotation(Cursor.class)))
                    .forEach(
                            it -> {
                                var fields =
                                        getFieldsName(
                                                getOneGeneric(it.asType()).orElse(it.asType()));
                                var uuidItem = UUID.randomUUID().toString().replace("-", "");
                                var child =
                                        new ClassTree(
                                                it.toString(),
                                                uuidItem,
                                                it.asType(),
                                                item,
                                                new ArrayList<>(),
                                                fields);
                                item.children().add(child);
                                queue.add(child);
                            });
        }

        return root;
    }

    private CodeBlock createRowMapper(ClassTree root) {
        var builder = CodeBlock.builder();
        var stack = new ArrayDeque<>(root.children());
        var rootResultSet = "rs";

        builder.add("return (%s, rowNum) -> {".formatted(rootResultSet) + "\n");
        builder.indent();

        builder.add(createFields(root, rootResultSet));

        while (!stack.isEmpty()) {
            if (stack.getLast().children().isEmpty()) {
                var item = stack.pollLast();

                var typeMirror = getOneGeneric(item.typeMirror()).orElse(item.typeMirror());
                var nameResultSet = "rs1";

                builder.add(
                        "$T %s = (%s, rowNum1) -> {"
                                        .formatted(
                                                item.name() + item.parent().uuid(), nameResultSet)
                                + "\n",
                        ParameterizedTypeName.get(
                                ClassName.get(RowMapper.class), TypeName.get(typeMirror)));
                builder.indent();

                builder.add(createFields(item, nameResultSet));

                builder.add(createNewObject(item));

                builder.addStatement("return %s".formatted(item.name() + item.uuid()));
                builder.unindent();
                builder.add("};\n");

                item.parent().children().remove(item);
            } else {
                stack.addAll(stack.getLast().children());
            }
        }

        builder.add(createNewObject(root));

        builder.addStatement("return %s".formatted(root.name() + root.uuid()));
        builder.unindent();
        builder.add("};");
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
                                        getColumnName(it),
                                        TypeName.get(it.asType())));
        return builder.build();
    }

    private CodeBlock createNewObject(ClassTree item) {
        var typeMirror = getOneGeneric(item.typeMirror()).orElse(item.typeMirror());
        if (ElementKind.RECORD.equals(
                processingEnvironment.getTypeUtils().asElement(typeMirror).getKind())) {
            return createNewRecord(item, typeMirror);
        }
        return createNewClass(item, typeMirror);
    }

    private CodeBlock createNewClass(ClassTree item, TypeMirror typeMirror) {
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
                                                        generateSetMethodName(it.toString()),
                                                        it.getSimpleName().toString()
                                                                + item.uuid()));
                                return;
                            }
                            var genericOption = getOneGeneric(it.asType());
                            var template = "%s.%s(%s(%s, ($T) rs.getObject($S)))";

                            if (genericOption.isPresent()) {
                                builder.addStatement(
                                        template.formatted(
                                                item.name() + item.uuid(),
                                                generateSetMethodName(it.toString()),
                                                EXTRACT_ROW_MAPPER,
                                                it.getSimpleName().toString() + item.uuid()),
                                        ResultSet.class,
                                        getColumnName(it));
                                return;
                            }
                            builder.addStatement(
                                    template.formatted(
                                            item.name() + item.uuid(),
                                            generateSetMethodName(it.toString()),
                                            EXTRACT_ROW_MAPPER_ONE,
                                            it.getSimpleName().toString() + item.uuid()),
                                    ResultSet.class,
                                    getColumnName(it));
                        });
        return builder.build();
    }

    private CodeBlock createNewRecord(ClassTree item, TypeMirror typeMirror) {
        var builder = CodeBlock.builder();
        builder.add(
                "var %s = new $T(".formatted(item.name() + item.uuid()) + "\n",
                TypeName.get(typeMirror));
        IntStream.range(0, item.fields().size())
                .forEach(
                        index -> {
                            var templateExtract = "%s(%s, ($T) rs.getObject($S))%s";
                            var template = "%s%s";
                            var postfix = index == item.fields().size() - 1 ? ");" : ",";
                            var genericOption = getOneGeneric(item.fields().get(index).asType());

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
                                                        EXTRACT_ROW_MAPPER,
                                                        item.fields()
                                                                        .get(index)
                                                                        .getSimpleName()
                                                                        .toString()
                                                                + item.uuid(),
                                                        postfix)
                                                + "\n",
                                        ResultSet.class,
                                        getColumnName(item.fields().get(index)));
                                return;
                            }
                            builder.add(
                                    templateExtract.formatted(
                                                    EXTRACT_ROW_MAPPER_ONE,
                                                    item.fields()
                                                                    .get(index)
                                                                    .getSimpleName()
                                                                    .toString()
                                                            + item.uuid(),
                                                    postfix)
                                            + "\n",
                                    ResultSet.class,
                                    getColumnName(item.fields().get(index)));
                        });
        return builder.build();
    }
}
