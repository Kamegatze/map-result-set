package io.github.kamegatze.map.result.set.processor.impl;

import io.github.kamegatze.map.result.set.Cursor;
import io.github.kamegatze.map.result.set.processor.ClassTree;
import io.github.kamegatze.map.result.set.processor.ClassTreeService;
import io.github.kamegatze.map.result.set.processor.utilities.CodeUtility;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ClassTreeServiceImpl implements ClassTreeService {
  private final ProcessingEnvironment processingEnvironment;

  public ClassTreeServiceImpl(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
  }

  @Override
  public ClassTree createTree(List<VariableElement> variableElements, TypeMirror rootType) {
    String uuidRoot = UUID.randomUUID().toString().replace("-", "");
    ClassTree root =
        new ClassTreeSimple(
            GeneralConstantUtility.ROOT_VARIABLE_ROW_MAPPER,
            uuidRoot,
            rootType,
            variableElements,
            null,
            new ArrayList<>());

    Deque<ClassTree> queue = new ArrayDeque<>();
    queue.add(root);

    while (!queue.isEmpty()) {
      ClassTree item = queue.pollFirst();

      item.fields().stream()
          .filter(it -> Objects.nonNull(it.getAnnotation(Cursor.class)))
          .forEach(
              it -> {
                List<VariableElement> fields =
                    CodeUtility.getFieldsName(
                        CodeUtility.getOneGeneric(it.asType()).orElse(it.asType()),
                        processingEnvironment);
                String uuidItem = UUID.randomUUID().toString().replace("-", "");
                ClassTree child =
                    new ClassTreeSimple(
                        it.toString(), uuidItem, it.asType(), fields, item, new ArrayList<>());
                item.children().add(child);
                queue.add(child);
              });
    }

    return root;
  }
}
