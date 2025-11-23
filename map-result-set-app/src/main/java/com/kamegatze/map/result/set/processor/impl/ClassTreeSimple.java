package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.processor.ClassTree;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

record ClassTreeSimple(
    String name,
    String uuid,
    TypeMirror typeMirror,
    List<VariableElement> fields,
    ClassTree parent,
    List<ClassTree> children)
    implements ClassTree {

  @Override
  @SuppressWarnings("NullableProblems")
  public String toString() {
    return getStructTree().toString();
  }

  private StringBuilder getStructTree() {
    var root = getRoot();
    var builder = new StringBuilder();

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
