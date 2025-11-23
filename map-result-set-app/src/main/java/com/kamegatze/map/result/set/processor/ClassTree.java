package com.kamegatze.map.result.set.processor;

import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public interface ClassTree {
  String name();

  String uuid();

  TypeMirror typeMirror();

  ClassTree parent();

  List<ClassTree> children();

  List<VariableElement> fields();
}
