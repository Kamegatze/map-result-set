package com.kamegatze.map.result.set.processor;

import com.palantir.javapoet.CodeBlock;

public interface GenerateRowMapper {

    CodeBlock generate(ClassTree root);
}
