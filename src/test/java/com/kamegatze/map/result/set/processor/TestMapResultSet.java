package com.kamegatze.map.result.set.processor;

import com.kamegatze.map.result.set.MapResultSet;
import org.springframework.jdbc.core.RowMapper;


@MapResultSet
public interface TestMapResultSet {

    RowMapper<ObjectDatabase> rowMapper();

}
