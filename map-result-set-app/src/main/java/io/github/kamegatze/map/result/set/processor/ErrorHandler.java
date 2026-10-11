package io.github.kamegatze.map.result.set.processor;

import java.util.concurrent.Callable;

public interface ErrorHandler<T> {
    T handleCallable(Callable<T> callable);
}
