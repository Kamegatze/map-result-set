package io.github.kamegatze.map.result.set.processor;

import com.squareup.javapoet.CodeBlock;

public interface GenerateResultSetMapper {

  CodeBlock generate(ClassTree root);
}
