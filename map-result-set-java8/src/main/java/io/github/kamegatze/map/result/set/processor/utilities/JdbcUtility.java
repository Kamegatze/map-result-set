package io.github.kamegatze.map.result.set.processor.utilities;

import io.github.kamegatze.map.result.set.processor.exception.SQLCloseResourceException;
import java.sql.ResultSet;
import java.util.Objects;

public final class JdbcUtility {
  private JdbcUtility() {}

  public static void close(ResultSet resultSet) {
    if (Objects.isNull(resultSet)) {
      return;
    }
    try {
      resultSet.close();
    } catch (Exception e) {
      throw new SQLCloseResourceException(e);
    }
  }
}
