package com.kamegatze.map.result.set.processor.impl;

import com.kamegatze.map.result.set.Cursor;
import com.kamegatze.map.result.set.processor.ClassTree;
import com.kamegatze.map.result.set.processor.ClassTreeService;
import com.kamegatze.map.result.set.processor.utilities.CodeUtility;
import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public record ClassTreeServiceImpl(ProcessingEnvironment processingEnvironment)
    implements ClassTreeService {

  @Override
  public ClassTree createTree(List<VariableElement> variableElements, TypeMirror rootType) {
    var uuidRoot = UUID.randomUUID().toString().replace("-", "");
    var root =
        new ClassTreeSimple(
            GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER,
            uuidRoot,
            rootType,
            variableElements,
            null,
            new ArrayList<>());

    var queue = new ArrayDeque<ClassTreeSimple>();
    queue.add(root);

    while (!queue.isEmpty()) {
      var item = queue.pollFirst();

      item.fields().stream()
          .filter(it -> Objects.nonNull(it.getAnnotation(Cursor.class)))
          .forEach(
              it -> {
                var fields =
                    CodeUtility.getFieldsName(
                        CodeUtility.getOneGeneric(it.asType()).orElse(it.asType()),
                        processingEnvironment);
                var uuidItem = UUID.randomUUID().toString().replace("-", "");
                var child =
                    new ClassTreeSimple(
                        it.toString(), uuidItem, it.asType(), fields, item, new ArrayList<>());
                item.children().add(child);
                queue.add(child);
              });
    }

    return root;
  }
}
