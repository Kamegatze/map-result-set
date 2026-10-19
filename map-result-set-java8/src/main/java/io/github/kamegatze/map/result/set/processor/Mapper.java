package io.github.kamegatze.map.result.set.processor;

@FunctionalInterface
public interface Mapper<T, R> {

  R map(T t);
}
