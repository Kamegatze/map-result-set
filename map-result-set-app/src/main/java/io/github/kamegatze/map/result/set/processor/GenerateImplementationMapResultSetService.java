package io.github.kamegatze.map.result.set.processor;

import com.squareup.javapoet.JavaFile;

import javax.lang.model.element.Element;

public interface GenerateImplementationMapResultSetService {

  JavaFile generate(Element element);

  void write(JavaFile javaFile, Element element);
}
