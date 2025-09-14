package com.kamegatze.map.result.set.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetMapper<T> {

    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
