package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.processor.ClassTree;

import java.util.List;
import java.util.Objects;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

class ClassTreeSimple implements ClassTree {

  private final String name;
  private final String uuid;
  private final TypeMirror typeMirror;
  private final List<VariableElement> fields;
  private final ClassTree parent;
  private final List<ClassTree> children;

  public ClassTreeSimple(
      String name,
      String uuid,
      TypeMirror typeMirror,
      List<VariableElement> fields,
      ClassTree parent,
      List<ClassTree> children) {
    this.name = name;
    this.uuid = uuid;
    this.typeMirror = typeMirror;
    this.fields = fields;
    this.parent = parent;
    this.children = children;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String uuid() {
    return this.uuid;
  }

  @Override
  public TypeMirror typeMirror() {
    return this.typeMirror;
  }

  @Override
  public ClassTree parent() {
    return this.parent;
  }

  @Override
  public List<ClassTree> children() {
    return this.children;
  }

  @Override
  public List<VariableElement> fields() {
    return this.fields;
  }

  @Override
  public String toString() {
    return getStructTree().toString();
  }

  private StringBuilder getStructTree() {
    ClassTree root = getRoot();
    StringBuilder builder = new StringBuilder();

    mutateStructTree(builder, root);

    return builder;
  }

  private void mutateStructTree(StringBuilder stringBuilder, ClassTree item) {
    if (item.children().isEmpty()) {
      stringBuilder
          .append("{name=\"")
          .append(item.name())
          .append("\", uuid=\"")
          .append(item.uuid())
          .append("\", typeMirror=")
          .append(item.typeMirror())
          .append(", fields=")
          .append(item.fields())
          .append(", children=[]}");
      return;
    }
    stringBuilder
        .append("{name=\"")
        .append(item.name())
        .append("\", uuid=\"")
        .append(item.uuid())
        .append("\", typeMirror=")
        .append(item.typeMirror())
        .append(", fields=")
        .append(item.fields())
        .append(", children=[");
    for (int i = 0; i < item.children().size(); i++) {
      mutateStructTree(stringBuilder, item.children().get(i));
      if (i + 1 != item.children().size()) {
        stringBuilder.append(", ");
      }
    }
    stringBuilder.append("]}");
  }

  private ClassTree getRoot() {
    ClassTree current = this;
    while (Objects.nonNull(current.parent())) {
      current = current.parent();
    }
    return current;
  }
}
