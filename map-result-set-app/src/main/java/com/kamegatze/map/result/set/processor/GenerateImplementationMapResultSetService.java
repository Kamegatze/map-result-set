package com.kamegatze.map.result.set.processor;

import com.palantir.javapoet.JavaFile;
import javax.lang.model.element.Element;

public interface GenerateImplementationMapResultSetService {

  JavaFile generate(Element element);

  void write(JavaFile javaFile);
}
