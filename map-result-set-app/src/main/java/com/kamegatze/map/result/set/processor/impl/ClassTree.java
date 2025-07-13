package com.kamegatze.map.result.set.processor.impl;

import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

record ClassTree(
        String name,
        String uuid,
        TypeMirror typeMirror,
        ClassTree parent,
        List<ClassTree> children,
        List<VariableElement> fields) {}
