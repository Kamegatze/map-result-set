package com.kamegatze.map.result.set.logger;

import com.kamegatze.map.result.set.context.Context;
import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import javax.annotation.processing.ProcessingEnvironment;

public final class LoggerFactory {
  private LoggerFactory() {}

  public static System.Logger create(String loggerName) {
    var level =
        System.Logger.Level.valueOf(
            System.getProperty(
                    GeneralConstantUtility.LOGGING_LEVEL_KEY, System.Logger.Level.WARNING.getName())
                .toUpperCase());
    return new LoggerAnnotationPrecessing(
        loggerName, level, Context.get(ProcessingEnvironment.class));
  }

  public static System.Logger create(Class<?> clazz) {
    return create(clazz.getName());
  }
}
