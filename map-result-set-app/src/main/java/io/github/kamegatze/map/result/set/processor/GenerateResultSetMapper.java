package io.github.kamegatze.map.result.set.processor;

import com.palantir.javapoet.CodeBlock;

public interface GenerateResultSetMapper {

  CodeBlock generate(ClassTree root);
}
