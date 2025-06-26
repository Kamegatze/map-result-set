package com.kamegatze.map.result.set.processor;

import com.palantir.javapoet.JavaFile;

import javax.lang.model.element.Element;

public interface ProcessorAnnotation {

    JavaFile processor(Element element);

    void write(JavaFile javaFile);
}
