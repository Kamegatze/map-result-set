package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.processor.ClassTree;
import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

record ClassTreeSimple(
        String name,
        String uuid,
        TypeMirror typeMirror,
        ClassTree parent,
        List<ClassTree> children,
        List<VariableElement> fields)
        implements ClassTree {}
