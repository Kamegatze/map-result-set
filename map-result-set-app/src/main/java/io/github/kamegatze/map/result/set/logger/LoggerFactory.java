package io.github.kamegatze.map.result.set.logger;

import io.github.kamegatze.map.result.set.context.Context;
import io.github.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import javax.annotation.processing.ProcessingEnvironment;

public final class LoggerFactory {
  private LoggerFactory() {}

  public static Logger create(String loggerName) {
    Level level =
        Level.valueOf(
            System.getProperty(
                    GeneralConstantUtility.LOGGING_LEVEL_KEY, Level.WARNING.name())
                .toUpperCase());
    return new LoggerImpl(
        loggerName, level, Context.get(ProcessingEnvironment.class));
  }

  public static Logger create(Class<?> clazz) {
    return create(clazz.getName());
  }
}
