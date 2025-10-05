package com.kamegatze.map.result.set.processor.utilities;

import com.kamegatze.map.result.set.Column;
import com.kamegatze.map.result.set.Cursor;
import com.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

public final class CodeUtility {

    private CodeUtility() {}

    public static Optional<TypeMirror> getOneGeneric(TypeMirror typeMirror) {
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
                TypeMirror.class
                        + " not instanceof "
                        + DeclaredType.class
                        + ". Impossible get generic type from "
                        + typeMirror);
    }

    public static List<VariableElement> getFieldsName(
            TypeMirror typeMirror, ProcessingEnvironment processingEnvironment) {
        var elementsUtils = processingEnvironment.getElementUtils();
        var typesUtils = processingEnvironment.getTypeUtils();

        var element = typesUtils.asElement(typeMirror);
        var packageName = elementsUtils.getPackageOf(element);

        var typeElement =
                elementsUtils.getTypeElement(
                        packageName.getQualifiedName().toString()
                                + "."
                                + element.getSimpleName().toString());

        return ElementFilter.fieldsIn(
                processingEnvironment.getElementUtils().getAllMembers(typeElement));
    }

    public static String getColumnName(VariableElement variableElement) {
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

    public static String generateSetMethodName(String fieldName) {
        var fieldNameCopy = fieldName.startsWith("is") ? fieldName.substring(2) : fieldName;
        return "set" + fieldNameCopy.substring(0, 1).toUpperCase() + fieldNameCopy.substring(1);
    }
}
