package com.kamegatze.map.result.set.processor;

import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public interface ClassTreeService {
  ClassTree createTree(List<VariableElement> variableElements, TypeMirror rootType);
}
