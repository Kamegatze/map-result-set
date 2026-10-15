package io.github.kamegatze.map.result.set.processor;

import com.squareup.javapoet.CodeBlock;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

public interface GenerateNewObject {
  CodeBlock createNew(ClassTree item, TypeMirror typeMirror, String resultSetName);

  ElementKind type();
}
