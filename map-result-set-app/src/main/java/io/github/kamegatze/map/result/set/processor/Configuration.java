package io.github.kamegatze.map.result.set.processor;

import io.github.kamegatze.map.result.set.ConventionFieldPolicy;
import io.github.kamegatze.map.result.set.ConventionFieldPolicy.Policy;
import java.util.Map;
import javax.lang.model.type.TypeMirror;

public interface Configuration {

  Policy conventionFieldPolicy();

  Map<TypeMirror, ConventionFieldPolicy> conventionFieldPolicyMap();
}
