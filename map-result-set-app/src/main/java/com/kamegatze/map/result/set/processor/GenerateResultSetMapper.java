package com.kamegatze.map.result.set.processor;

import com.palantir.javapoet.CodeBlock;

public interface GenerateResultSetMapper {

    CodeBlock generate(ClassTree root);
}
