package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentClass;
import org.springframework.jdbc.core.RowMapper;

@MapResultSet
public interface StudentClassMapper {

    RowMapper<StudentClass> getRowMapper();
}
