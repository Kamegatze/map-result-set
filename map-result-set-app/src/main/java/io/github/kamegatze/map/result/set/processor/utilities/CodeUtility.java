package io.github.kamegatze.map.result.set.processor.utilities;

import io.github.kamegatze.map.result.set.Column;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy.Policy;
import io.github.kamegatze.map.result.set.Cursor;
import io.github.kamegatze.map.result.set.context.Context;
import io.github.kamegatze.map.result.set.processor.Configuration;
import io.github.kamegatze.map.result.set.processor.exception.MoreThenOneItemException;
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
            packageName.getQualifiedName().toString() + "." + element.getSimpleName().toString());

    return ElementFilter.fieldsIn(
        processingEnvironment.getElementUtils().getAllMembers(typeElement));
  }

  public static String getColumnName(VariableElement variableElement, Policy policy) {
    var column = variableElement.getAnnotation(Column.class);
    if (Objects.nonNull(column)) {
      return column.value();
    }
    var cursor = variableElement.getAnnotation(Cursor.class);
    if (Objects.nonNull(cursor) && !cursor.value().isBlank()) {
      return cursor.value();
    }
    return getColumnNameByConventionField(variableElement.getSimpleName().toString(), policy);
  }

  public static String generateSetMethodName(String fieldName) {
    var fieldNameCopy = fieldName.startsWith("is") ? fieldName.substring(2) : fieldName;
    return "set" + fieldNameCopy.substring(0, 1).toUpperCase() + fieldNameCopy.substring(1);
  }

  public static Policy getColumnPolicy(VariableElement variableElement, TypeMirror entityType) {
    var annotation = variableElement.getAnnotation(ConventionFieldPolicy.class);
    if (Objects.nonNull(annotation)) {
      return annotation.value();
    }
    annotation = Context.get(Configuration.class).conventionFieldPolicyMap().get(entityType);
    if (Objects.nonNull(annotation)) {
      return annotation.value();
    }
    return Context.get(Configuration.class).conventionFieldPolicy();
  }

  private static String getColumnNameByConventionField(String fieldName, Policy policy) {
    return switch (policy) {
      case CAMEL_CASE -> getCamelCaseFieldName(fieldName);
      case PASCAL_CASE -> getPascalCaseFieldName(fieldName);
      case SNAKE_CASE -> getSnakeCaseFieldName(fieldName);
    };
  }

  private static String getSnakeCaseFieldName(String fieldName) {
    if (Objects.isNull(fieldName)) {
      return "";
    }

    if (isSnakeCase(fieldName)) {
      return fieldName;
    }

    if (isCamelCase(fieldName) || isPascalCase(fieldName)) {
      return getSnakeCaseOrKebabCaseFromPascalCaseOrCamelCase(fieldName, '_');
    }

    return fieldName.replace('-', '_');
  }

  private static String getPascalCaseFieldName(String fieldName) {
    if (Objects.isNull(fieldName)) {
      return "";
    }

    if (isPascalCase(fieldName)) {
      return fieldName;
    }

    var result = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

    if (isCamelCase(fieldName)) {
      return result;
    }

    if (isSnakeCase(fieldName)) {
      return getCamelCaseFromKebabOrSnakeCase(result, '_');
    }

    return getCamelCaseFromKebabOrSnakeCase(result, '-');
  }

  private static String getCamelCaseFieldName(String fieldName) {
    if (Objects.isNull(fieldName)) {
      return "";
    }

    if (isCamelCase(fieldName)) {
      return fieldName;
    }

    if (isPascalCase(fieldName)) {
      return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    if (isSnakeCase(fieldName)) {
      return getCamelCaseFromKebabOrSnakeCase(fieldName, '_');
    }
    return getCamelCaseFromKebabOrSnakeCase(fieldName, '-');
  }

  private static String getSnakeCaseOrKebabCaseFromPascalCaseOrCamelCase(
      String fieldName, char splitCharacter) {
    if (Objects.isNull(fieldName)) {
      return "";
    }

    var result = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    for (int i = 0; i < fieldName.length(); i++) {
      if (Character.isUpperCase(fieldName.charAt(i))) {
        result = fieldName.substring(0, i) + splitCharacter + fieldName.substring(i).toLowerCase();
      }
    }
    return result;
  }

  private static String getCamelCaseFromKebabOrSnakeCase(String fieldName, char splitCharacter) {
    if (Objects.isNull(fieldName)) {
      return "";
    }

    var result = fieldName;
    for (int i = 0; i <= fieldName.length(); i++) {
      if (fieldName.charAt(i) == splitCharacter) {
        result =
            fieldName.substring(0, i)
                + Character.toUpperCase(fieldName.charAt(i + 1))
                + fieldName.substring(i + 2);
      }
    }
    return result;
  }

  private static boolean isSnakeCase(String fieldName) {
    return Objects.nonNull(fieldName)
        && !fieldName.isBlank()
        && fieldName.contains("_")
        && fieldName.toLowerCase().equals(fieldName);
  }

  private static boolean isPascalCase(String fieldName) {
    if (Objects.isNull(fieldName)) {
      return false;
    }
    if (fieldName.isBlank()) {
      return false;
    }

    for (int i = 1; i < fieldName.length(); i++) {
      if (Character.isUpperCase(fieldName.charAt(i))
          && Character.isUpperCase(fieldName.charAt(0))) {
        return true;
      }
    }

    return false;
  }

  private static boolean isCamelCase(String fieldName) {
    if (Objects.isNull(fieldName)) {
      return false;
    }

    if (fieldName.isBlank()) {
      return false;
    }

    for (int i = 1; i < fieldName.length(); i++) {
      if (fieldName.charAt(i) == '_'
          || fieldName.charAt(i) == '-'
          || Character.isUpperCase(fieldName.charAt(0))) {
        return false;
      }
    }
    return true;
  }
}
